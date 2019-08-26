package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.MessageEvent;
import org.codenergic.akinabot.core.QueueConfig;
import org.codenergic.akinatorj.AkinatorJ;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

@Service
class TelegramBotService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<Long, Session> sessions;

	private final AkinatorJ akinatorJ;
	private final TelegramBot telegramBot;
	private final Executor akinabotExecutor;
	private final BlockingQueue<Update> updateQueue;
	private final Consumer<MessageEvent> messageLogger;
	private final List<MessageHandler> messageHandlers;

	TelegramBotService(AkinatorJ akinatorJ, TelegramBot telegramBot, Executor akinabotExecutor, QueueConfig queueConfig, List<MessageHandler> messageHandlers) {
		logger.info("Initializing bot service, available message handlers: {}", messageHandlers);
		this.akinatorJ = akinatorJ;
		this.sessions = queueConfig.telegramSessions();
		this.akinabotExecutor = akinabotExecutor;
		this.telegramBot = telegramBot;
		this.updateQueue = queueConfig.telegramUpdateQueue();
		this.messageLogger = queueConfig.messageLoggerQueue()::add;
		this.messageHandlers = messageHandlers;
	}

	@PostConstruct
	void init() {
		Executors.newSingleThreadExecutor().submit(() -> {
			while (true) {
				try {
					Update update = updateQueue.take();
					Message message = update.message();
					if (message == null) continue;
					// handle updates in pooled thread
					akinabotExecutor.execute(() -> onUpdate(message));
					// publish bot.message metric
					messageLogger.accept(new MessageEvent(ChatProvider.TELEGRAM, MessageEvent.InOut.INBOUND,
							message.chat().id().toString(), message.from().username()));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private void onUpdate(Message message) {
		Long chatId = message.chat().id();
		MessageHandlerChain messageHandlerChain = new MessageHandlerChain(telegramBot, sessions, messageHandlers, messageLogger);
		Session session = Optional.ofNullable(sessions.get(chatId))
				.map(s -> s.bind(akinatorJ))
				.orElse(null);
		messageHandlerChain.handleMessage(session, message);
	}
}

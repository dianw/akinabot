package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.codenergic.akinabot.core.QueueConfig;
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

	private final TelegramBot telegramBot;
	private final Executor asyncExecutor;
	private final BlockingQueue<Update> updateQueue;
	private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
	private final List<MessageHandler> messageHandlers;

	TelegramBotService(TelegramBot telegramBot, Executor asyncExecutor, QueueConfig queueConfig, List<MessageHandler> messageHandlers) {
		logger.info("Initializing bot service, available message handlers: {}", messageHandlers);
		this.asyncExecutor = asyncExecutor;
		this.telegramBot = telegramBot;
		this.updateQueue = queueConfig.getUpdateQueue();
		this.messageHandlers = messageHandlers;
	}

	@PostConstruct
	void init() {
		Executors.newSingleThreadExecutor().submit(() -> {
			while (true) {
				Update update = updateQueue.take();
				asyncExecutor.execute(() -> onUpdate(update));
			}
		});
	}

	private void onUpdate(Update update) {
		Message message = update.message();
		if (message == null) return;
		Long chatId = message.chat().id();
		MessageHandlerChain messageHandlerChain = new MessageHandlerChain(telegramBot, sessions, messageHandlers);
		messageHandlerChain.handleMessage(sessions.get(chatId), message);
	}
}

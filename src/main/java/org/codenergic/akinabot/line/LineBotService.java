package org.codenergic.akinabot.line;

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

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.source.Source;

@Service
class LineBotService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<Source, Session> sessions;

	private final AkinatorJ akinatorJ;
	private final LineMessagingClient lineMessagingClient;
	private final Executor akinabotExecutor;
	private final BlockingQueue<Event> lineEventQueue;
	private final Consumer<MessageEvent> messageLogger;
	private final List<MessageHandler> messageHandlers;

	LineBotService(AkinatorJ akinatorJ, LineMessagingClient lineMessagingClient, Executor akinabotExecutor, QueueConfig queueConfig, List<MessageHandler> messageHandlers) {
		logger.info("Initializing bot service, available message handlers: {}", messageHandlers);
		this.akinatorJ = akinatorJ;
		this.sessions = queueConfig.lineSessions();
		this.lineMessagingClient = lineMessagingClient;
		this.akinabotExecutor = akinabotExecutor;
		this.lineEventQueue = queueConfig.lineEventQueue();
		this.messageLogger = queueConfig.messageLoggerQueue()::add;
		this.messageHandlers = messageHandlers;
	}

	@PostConstruct
	void init() {
		Executors.newSingleThreadExecutor().submit(() -> {
			while (true) {
				try {
					Event event = lineEventQueue.take();
					// handle updates in pooled thread
					akinabotExecutor.execute(() -> onUpdate(event));
					// publish bot.message metric
					Source eventSource = event.getSource();
					messageLogger.accept(new MessageEvent(ChatProvider.LINE, MessageEvent.InOut.INBOUND,
							eventSource.getSenderId(), eventSource.getSenderId()));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private void onUpdate(Event event) {
		MessageHandlerChain messageHandlerChain = new MessageHandlerChain(lineMessagingClient, sessions, messageHandlers, messageLogger);
		Session session = Optional.ofNullable(sessions.get(event.getSource()))
				.map(s -> s.bind(akinatorJ))
				.orElse(null);
		messageHandlerChain.handleMessage(session, event);
	}
}

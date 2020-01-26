package org.codenergic.akinabot.line;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
	private final List<MessageHandler> messageHandlers;
	private final ExecutorService queueExecutor = Executors.newSingleThreadExecutor();

	private boolean active = true;

	LineBotService(AkinatorJ akinatorJ, LineMessagingClient lineMessagingClient, Executor akinabotExecutor, QueueConfig queueConfig, List<MessageHandler> messageHandlers) {
		logger.info("Initializing bot service, available message handlers: {}", messageHandlers);
		this.akinatorJ = akinatorJ;
		this.sessions = queueConfig.lineSessions();
		this.lineMessagingClient = lineMessagingClient;
		this.akinabotExecutor = akinabotExecutor;
		this.lineEventQueue = queueConfig.lineEventQueue();
		this.messageHandlers = messageHandlers;
	}

	@PostConstruct
	void init() {
		queueExecutor.submit(() -> {
			while (active) {
				try {
					Event event = lineEventQueue.take();
					// handle updates in pooled thread
					akinabotExecutor.execute(() -> onUpdate(event));
					// publish bot.message metric
					Source eventSource = event.getSource();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	@PreDestroy
	void shutdown() {
		active = false;
		queueExecutor.shutdown();
	}

	private void onUpdate(Event event) {
		MessageHandlerChain messageHandlerChain = new MessageHandlerChain(lineMessagingClient, sessions, messageHandlers);
		Session session = Optional.ofNullable(sessions.get(event.getSource()))
				.map(s -> s.bind(akinatorJ))
				.orElse(null);
		messageHandlerChain.handleMessage(session, event);
	}
}

package org.codenergic.akinabot.core;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class MessageEventListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final BlockingQueue<MessageEvent> messageLoggerQueue;
	private final MeterRegistry meterRegistry;

	public MessageEventListener(QueueConfig queueConfig, MeterRegistry meterRegistry) {
		logger.info("Initializing message event listener");
		this.messageLoggerQueue = queueConfig.messageLoggerQueue();
		this.meterRegistry = meterRegistry;
	}

	@PostConstruct
	public void listenToMessageQueue() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			logger.info("Start listening to message event");
			while (true) {
				try {
					sendMetric(messageLoggerQueue.take());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private void sendMetric(MessageEvent event) {
		if (Stream.of(event.getChatProvider(), event.getInOut(), event.getUsername(), event.getChatId())
				.anyMatch(Objects::isNull)) {
			logger.warn("Skipping counter: {}", event);
			return;
		}
		meterRegistry.counter("bot.messages",
				"provider", event.getChatProvider().toString().toLowerCase(),
				"in_out", event.getInOut().toString().toLowerCase(),
				"username", event.getUsername(),
				"chat_id", event.getChatId())
				.increment();
	}
}

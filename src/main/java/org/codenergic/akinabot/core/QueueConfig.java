package org.codenergic.akinabot.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.linecorp.bot.model.event.Event;
import com.pengrad.telegrambot.model.Update;

@Component
public class QueueConfig {
	private final BlockingQueue<Event> lineEventQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<Update> telegramUpdateQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<MessageEvent> messageLoggerQueue = new LinkedBlockingQueue<>();

	public BlockingQueue<Event> lineEventQueue() {
		return lineEventQueue;
	}

	public BlockingQueue<MessageEvent> messageLoggerQueue() {
		return messageLoggerQueue;
	}

	public BlockingQueue<Update> telegramUpdateQueue() {
		return telegramUpdateQueue;
	}
}

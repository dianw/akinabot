package org.codenergic.akinabot.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.codenergic.akinatorj.Session;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.source.Source;
import com.pengrad.telegrambot.model.Update;

@Component
public class QueueConfig {
	private final BlockingQueue<Event> lineEventQueue;
	private final BlockingQueue<Update> telegramUpdateQueue;
	private final BlockingQueue<MessageEvent> messageLoggerQueue;
	private final Map<Source, Session> lineSessions;
	private final Map<Long, Session> telegramSessions;

	public QueueConfig(HazelcastInstance hz) {
		this.lineEventQueue = hz.getQueue("lineEventQueue");
		this.telegramUpdateQueue = hz.getQueue("telegramUpdateQueue");
		this.messageLoggerQueue = hz.getQueue("messageLoggerQueue");
		this.lineSessions = hz.getMap("lineSessions");
		this.telegramSessions = hz.getMap("telegramSessions");
	}

	public BlockingQueue<Event> lineEventQueue() {
		return lineEventQueue;
	}

	public Map<Source, Session> lineSessions() {
		return lineSessions;
	}

	public BlockingQueue<MessageEvent> messageLoggerQueue() {
		return messageLoggerQueue;
	}

	public Map<Long, Session> telegramSessions() {
		return telegramSessions;
	}

	public BlockingQueue<Update> telegramUpdateQueue() {
		return telegramUpdateQueue;
	}
}

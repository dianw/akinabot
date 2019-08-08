package org.codenergic.akinabot.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Update;

@Component
public class QueueConfig {
	private final BlockingQueue<Update> updateQueue = new LinkedBlockingQueue<>();

	public BlockingQueue<Update> getUpdateQueue() {
		return updateQueue;
	}
}

package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

@Service
@ConditionalOnProperty(name = "telegram.webhook", havingValue = "false")
public class TelegramBotPollService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final TelegramBot telegramBot;
	private final BlockingQueue<Update> updateQueue;
	private final AtomicInteger offset = new AtomicInteger();

	public TelegramBotPollService(TelegramBot telegramBot, QueueConfig queueConfig) {
		logger.info("{} Running bot in polling mode", ChatProvider.TELEGRAM);
		this.telegramBot = telegramBot;
		this.updateQueue = queueConfig.telegramUpdateQueue();
	}

	@Scheduled(fixedRateString = "${telegram.poll-rate:2000}")
	public void pollUpdates() {
		logger.trace("Fetching telegram updates");
		GetUpdatesResponse updatesResponse = telegramBot.execute(new GetUpdates()
				.limit(100).offset(offset.incrementAndGet()));
		if (!updatesResponse.isOk()) return;
		List<Update> updates = updatesResponse.updates();
		logger.trace("Updates fetched, no of updates: {}", updates.size());
		if (!updates.isEmpty()) {
			offset.set(updates.get(updates.size() - 1).updateId());
		}
		updateQueue.addAll(updates);
	}
}

package org.codenergic.akinabot.telegram;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import javax.servlet.http.HttpServletRequest;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;

@RestController
@ConditionalOnProperty(name = "telegram.webhook", havingValue = "true")
public class TelegramBotWebhookService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final BlockingQueue<Update> updateQueue;

	public TelegramBotWebhookService(QueueConfig queueConfig) {
		logger.info("{} Running bot in webhook mode", ChatProvider.TELEGRAM);
		this.updateQueue = queueConfig.getUpdateQueue();
	}

	@PostMapping("/bot/telegram")
	public String getUpdate(HttpServletRequest request) throws IOException {
		try (BufferedReader reader = request.getReader()) {
			Update update = BotUtils.parseUpdate(reader);
			updateQueue.add(update);
		}
		return "OK";
	}
}

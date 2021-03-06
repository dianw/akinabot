package org.codenergic.akinabot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.pengrad.telegrambot.TelegramBot;

@Configuration
public class TelegramBotConfig {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public TelegramBot telegramBot(@Value("${telegram.token:}") String token) {
		log.info("Configuring TelegramBot instance");
		if (StringUtils.isEmpty(token)) throw new IllegalArgumentException("Telegram bot token is required");
		return new TelegramBot(token);
	}
}

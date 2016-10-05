package akinabot.verticle.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;

@Configuration
public class TelegramBotModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public TelegramBot telegramBot(@Value("${telegram.bot.token}") String token) {
		log.info("Configuring TelegramBot instance");
		
		return TelegramBotAdapter.build(token);
	}
}

package akinabot.verticle.di;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;

import io.vertx.core.Vertx;

public class TelegramBotModule extends AbstractModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Provides
	@Singleton
	public TelegramBot telegramBot(Vertx vertx) {
		log.info("Providing TelegramBot instance");
		
		return TelegramBotAdapter.build(vertx.getOrCreateContext().config().getString("bot.token"));
	}

	@Override
	protected void configure() {
	}
}

package akinabot.verticle.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

@Configuration
public class VertxModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public Vertx vertx() {
		log.info("Configuring Vertx instance");
		
		return Vertx.vertx();
	}

	@Bean
	public EventBus eventBus(Vertx vertx) {
		log.info("Configuring Vertx EventBus");

		return vertx.eventBus();
	}
}

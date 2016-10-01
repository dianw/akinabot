package akinabot.verticle.di;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;

public class VertxModule extends AbstractModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Vertx vertx;

	public VertxModule(Vertx vertx) {
		this.vertx = vertx;
	}

	@Provides
	@Singleton
	public Vertx vertx() {
		log.info("Providing Vertx instance");

		return vertx;
	}

	@Override
	protected void configure() {
	}
}

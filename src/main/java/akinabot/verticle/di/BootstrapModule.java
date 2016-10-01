package akinabot.verticle.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;

public class BootstrapModule extends AbstractModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private Vertx vertx;

	public BootstrapModule(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	protected void configure() {
		log.info("Bootstrapping modules");

		install(new VertxModule(vertx));
		install(new TelegramBotModule());
		install(new FstModule());
		install(new CodecModule());
	}
}

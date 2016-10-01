package akinabot.verticle.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.JavaVerticleFactory;
import io.vertx.core.spi.VerticleFactory;

public class InjectorVerticleFactory extends JavaVerticleFactory {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private Injector injector;

	@Override
	public void init(Vertx vertx) {
		log.info("Initializing injector");

		injector = Guice.createInjector(new BootstrapModule(vertx));
	}

	@Override
	public String prefix() {
		return "java-inject";
	}

	@Override
	public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
		log.info("Deploying verticle: {}", VerticleFactory.removePrefix(verticleName));
		Verticle verticle = super.createVerticle(verticleName, classLoader);
		log.info("Injecting verticle: {}", VerticleFactory.removePrefix(verticleName));
		injector.injectMembers(verticle);

		return verticle;
	}

	public Injector getInjector() {
		return injector;
	}
}

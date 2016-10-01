package akinabot.verticle.di;

import javax.inject.Singleton;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FstModule extends AbstractModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Provides
	@Singleton
	public FSTConfiguration fstConfiguration() {
		log.info("Providing FST");

		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
		conf.setForceSerializable(true);
		return conf;
	}
	
	@Override
	protected void configure() {
	}
}

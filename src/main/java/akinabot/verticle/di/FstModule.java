package akinabot.verticle.di;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FstModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public FSTConfiguration fstConfiguration() {
		log.info("Configuring FST");

		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
		conf.setForceSerializable(true);
		return conf;
	}
}

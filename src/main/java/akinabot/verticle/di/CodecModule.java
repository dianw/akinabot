package akinabot.verticle.di;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akinabot.verticle.codec.FSTCodec;

@Configuration
public class CodecModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public FSTCodec fstCodec(FSTConfiguration conf) {
		log.info("Configuring codec: {}", FSTCodec.class.getName());
		
		return new FSTCodec(conf);
	}
}

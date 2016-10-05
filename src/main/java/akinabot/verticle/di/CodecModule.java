package akinabot.verticle.di;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akinabot.verticle.codec.QuestionAnswerCodec;
import akinabot.verticle.codec.UpdateCodec;

@Configuration
public class CodecModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public QuestionAnswerCodec questionAnswerCodec(FSTConfiguration conf) {
		log.info("Configuring codec: {}", QuestionAnswerCodec.class.getName());

		return new QuestionAnswerCodec(conf);
	}

	@Bean
	public UpdateCodec updateCodec(FSTConfiguration conf) {
		log.info("Configuring codec: {}", UpdateCodec.class.getName());

		return new UpdateCodec(conf);
	}
}

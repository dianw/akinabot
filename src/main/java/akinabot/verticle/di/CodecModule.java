package akinabot.verticle.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import akinabot.verticle.codec.QuestionAnswerCodec;
import akinabot.verticle.codec.UpdateCodec;

public class CodecModule extends AbstractModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configure() {
		log.info("Configuring codecs");

		bind(QuestionAnswerCodec.class).in(Scopes.SINGLETON);
		bind(UpdateCodec.class).in(Scopes.SINGLETON);
	}
}

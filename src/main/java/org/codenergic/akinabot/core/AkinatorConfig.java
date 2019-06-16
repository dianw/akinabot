package org.codenergic.akinabot.core;

import org.codenergic.akinatorj.AkinatorJ;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

@Configuration
public class AkinatorConfig {
	@Bean
	public AkinatorJ akinatorJ(ObjectMapper objectMapper) {
		return new AkinatorJ(new OkHttpClient(), objectMapper);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}
}

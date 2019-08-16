package org.codenergic.akinabot.line;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineSignatureValidator;

@Configuration
public class LineBotConfig {
	@Bean
	public LineMessagingClient lineMessagingClient(@Value("${line.token:}") String token) {
		return LineMessagingClient.builder(token).build();
	}

	@Bean
	public LineSignatureValidator lineSignatureValidator(@Value("${line.secret:}") String secret) {
		return new LineSignatureValidator(secret.getBytes());
	}
}

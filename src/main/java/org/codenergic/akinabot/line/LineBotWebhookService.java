package org.codenergic.akinabot.line;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import javax.servlet.http.HttpServletRequest;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;

@RestController
public class LineBotWebhookService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();

	private final BlockingQueue<Event> lineEventQueue;
	private final LineSignatureValidator lineSignatureValidator;

	public LineBotWebhookService(QueueConfig queueConfig, LineSignatureValidator lineSignatureValidator) {
		logger.info("{} Running bot in webhook mode", ChatProvider.LINE);
		this.lineEventQueue = queueConfig.lineEventQueue();
		this.lineSignatureValidator = lineSignatureValidator;
	}

	@PostMapping("/bot/line${line.secret}")
	public String handleDefaultMessageEvent(@RequestBody String payload, HttpServletRequest request) throws IOException {
		String signature = request.getHeader("X-Line-Signature");
		CallbackRequest callbackRequest = verifySignatureAndHandlePayload(signature, payload);
		lineEventQueue.addAll(callbackRequest.getEvents());
		return "OK";
	}

	private CallbackRequest verifySignatureAndHandlePayload(String signature, String payload) throws IOException {
		// validate signature
		if (signature == null || signature.length() == 0) {
			throw new IllegalStateException("Missing 'X-Line-Signature' header");
		}

		final byte[] json = payload.getBytes(StandardCharsets.UTF_8);

		if (!lineSignatureValidator.validateSignature(json, signature)) {
			throw new IllegalStateException("Invalid API signature");
		}

		final CallbackRequest callbackRequest = objectMapper.readValue(json, CallbackRequest.class);
		if (callbackRequest == null || callbackRequest.getEvents() == null) {
			throw new IllegalStateException("Invalid content");
		}
		return callbackRequest;
	}
}

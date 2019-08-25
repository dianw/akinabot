package org.codenergic.akinabot.telegram.handler;

import java.io.IOException;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinabot.telegram.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.response.BaseResponse;

@Service("telegramTypingActionHandler")
@Order(Ordered.LOWEST_PRECEDENCE - 100)
class TypingActionHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Message message) {
		return true;
	}

	@Override
	public void handleMessage(Session session, Message message, MessageHandlerChain chain) {
		chain.executeTelegramRequest(message.chat(), message.from(),
				new SendChatAction(message.chat().id(), ChatAction.typing), new Callback<SendChatAction, BaseResponse>() {
					@Override
					public void onResponse(SendChatAction request, BaseResponse response) {
						// do nothing
					}

					@Override
					public void onFailure(SendChatAction request, IOException e) {
						// do nothing
					}
				});
		logger.debug("{} [{}] Sending typing action message", ChatProvider.TELEGRAM, message.chat().id());
		chain.handleMessage(session, message);
	}
}

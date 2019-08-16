package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinabot.telegram.MessageHandlerChain;
import org.codenergic.akinatorj.AkinatorJ;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Service
@Order(Ordered.LOWEST_PRECEDENCE - 50)
class StartGameHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final AkinatorJ akinatorJ;

	StartGameHandler(AkinatorJ akinatorJ) {
		this.akinatorJ = akinatorJ;
	}

	@Override
	public boolean acceptMessage(Session session, Message message) {
		String text = message.text();
		return text != null && (text.equalsIgnoreCase(AnswerButtons.PLAY_NOW.getText())
				|| text.equalsIgnoreCase(AnswerButtons.PLAY_AGAIN.getText()));
	}

	@Override
	public void handleMessage(Session session, Message message, MessageHandlerChain chain) {
		String[] servers = {"en", "en2", "en3"};
		for (String server : servers) {
			try {
				Session newSession = akinatorJ.newSession(server);
				if (newSession != null) {
					logger.debug("{} [{}] Starting game on server [{}]", ChatProvider.TELEGRAM, message.chat().id(), server);
					chain.handleMessage(newSession, message);
					return;
				}
			} catch (IllegalStateException e) {
				logger.error("Server [{}] down: [{}]", server, e.getMessage(), e);
			}
		}
		chain.getTelegramBot().execute(new SendMessage(message.chat().id(), Texts.PROBLEM.getText()));
	}
}

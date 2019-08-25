package org.codenergic.akinabot.line.handler;

import java.util.Optional;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.line.MessageHandler;
import org.codenergic.akinabot.line.MessageHandlerChain;
import org.codenergic.akinatorj.AkinatorJ;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.message.TextMessage;

@Service("lineStartGameHandler")
@Order(Ordered.LOWEST_PRECEDENCE - 50)
class StartGameHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final AkinatorJ akinatorJ;

	StartGameHandler(AkinatorJ akinatorJ) {
		this.akinatorJ = akinatorJ;
	}

	@Override
	public boolean acceptMessage(Session session, Event event) {
		Optional<String> text = getMessageText(event);
		return text.isPresent() && (text.get().equalsIgnoreCase(AnswerButtons.PLAY_NOW.getText())
				|| text.get().equalsIgnoreCase(AnswerButtons.PLAY_AGAIN.getText()));
	}

	@Override
	public void handleMessage(Session session, Event event, MessageHandlerChain chain) {
		String[] servers = {"en", "en2", "en3"};
		for (String server : servers) {
			try {
				Session newSession = akinatorJ.newSession(server);
				if (newSession != null) {
					logger.debug("{} [{}] Starting game on server [{}]", ChatProvider.LINE, event.getSource().getSenderId(), server);
					chain.handleMessage(newSession, event);
					return;
				}
			} catch (IllegalStateException e) {
				logger.error("Server [{}] down: [{}]", server, e.getMessage(), e);
			}
		}
		chain.pushLineMessage(event.getSource().getSenderId(), TextMessage.builder()
				.text(Texts.PROBLEM.getText())
				.build());
	}
}

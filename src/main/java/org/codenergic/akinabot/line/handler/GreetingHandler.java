package org.codenergic.akinabot.line.handler;

import java.util.Optional;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.line.MessageHandler;
import org.codenergic.akinabot.line.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;

@Service("lineGreetingHandler")
class GreetingHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Event event) {
		Optional<String> text = getMessageText(event);
		return session == null || (text.isPresent() && (text.get().equalsIgnoreCase(AnswerButtons.START.getText())
				|| text.get().equalsIgnoreCase(AnswerButtons.QUIT.getText())));
	}

	@Override
	public void handleMessage(Session session, Event event, MessageHandlerChain chain) {
		chain.pushLineMessage(event.getSource().getSenderId(), TextMessage.builder()
				.text(Texts.GREETINGS.getText())
				.quickReply(QuickReply.builder()
						.item(QuickReplyItem.builder()
								.action(PostbackAction.builder()
										.label(AnswerButtons.PLAY_NOW.getText())
										.data(AnswerButtons.PLAY_NOW.getText())
										.displayText(AnswerButtons.PLAY_NOW.getText())
										.build())
								.build())
						.build())
				.build());
		logger.debug("{} [{}] Sending greeting message", ChatProvider.LINE, event.getSource().getSenderId());
		chain.handleMessage(null, event);
	}
}

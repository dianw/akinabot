package org.codenergic.akinabot.line;

import java.util.Optional;

import org.codenergic.akinatorj.Session;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;

public interface MessageHandler {
	boolean acceptMessage(Session session, Event event);

	void handleMessage(Session session, Event event, MessageHandlerChain chain);

	default Optional<String> getMessageText(Event event) {
		if (event instanceof MessageEvent) {
			MessageContent content = ((MessageEvent) event).getMessage();
			if (content instanceof TextMessageContent) {
				return Optional.of(((TextMessageContent) content).getText());
			}
		}
		if (event instanceof PostbackEvent) {
			PostbackContent content = ((PostbackEvent) event).getPostbackContent();
			return Optional.of(content.getData());
		}
		return Optional.empty();
	}
}

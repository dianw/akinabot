package org.codenergic.akinabot.line.handler;

import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.line.MessageHandler;
import org.codenergic.akinabot.line.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Element;
import org.codenergic.akinatorj.model.ListParameters;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TextMessage;

@Service("lineWinHandler")
@Order(Ordered.LOWEST_PRECEDENCE - 40)
class WinHandler implements MessageHandler {
	@Override
	public boolean acceptMessage(Session session, Event event) {
		if (session == null) return false;
		int step = Integer.parseInt(session.getCurrentStepInformation().getStep());
		return session.getProgression() >= 90d || step >= 35;
	}

	@Override
	public void handleMessage(Session session, Event event, MessageHandlerChain chain) {
		ListParameters parameters = session.win();
		if (parameters.getElements().isEmpty())
			chain.pushLineMessage(event.getSource().getSenderId(), TextMessage.builder()
					.text(Texts.CANT_GUESS.getText())
					.build());
		Element element = parameters.getElements().get(0).getElement();
		ImageMessage imageMessage = ImageMessage.builder()
				.originalContentUrl(element.getAbsolutePicturePath())
				.previewImageUrl(element.getAbsolutePicturePath())
				.build();
		TextMessage textMessage = TextMessage.builder()
				.text(Texts.RESULT.getText() + element.getName() + " (" + element.getDescription() + ")")
				.build();
		chain.pushLineMessage(event.getSource().getSenderId(), imageMessage, textMessage)
				.thenRun(() -> chain.handleMessage(null, event));
	}
}

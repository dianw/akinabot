package org.codenergic.akinabot.line.handler;

import java.util.Optional;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.line.MessageHandler;
import org.codenergic.akinatorj.Session;

import com.linecorp.bot.model.event.Event;

interface QuestionAnswerHandler extends MessageHandler {
	@Override
	default boolean acceptMessage(Session session, Event event) {
		Optional<String> text = getMessageText(event);
		if (session == null || !text.isPresent()
				|| text.get().equalsIgnoreCase(AnswerButtons.QUIT.getText())) return false;
		int step = Integer.parseInt(session.getCurrentStepInformation().getStep());
		return session.getProgression() < 90d && step < 35;
	}
}

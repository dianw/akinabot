package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinatorj.Session;

import com.pengrad.telegrambot.model.Message;

interface QuestionAnswerHandler extends MessageHandler {
	@Override
	default boolean acceptMessage(Session session, Message message) {
		if (session == null || message.text() == null
				|| message.text().equalsIgnoreCase(AnswerButtons.QUIT.getText())) return false;
		int step = Integer.parseInt(session.getCurrentStepInformation().getStep());
		return session.getProgression() < 90d && step < 35;
	}
}

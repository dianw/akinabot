package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QuestionAnswerUtils;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

@Service
class AnswerHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final QuestionHandler questionHandler;

	AnswerHandler(QuestionHandler questionHandler) {
		this.questionHandler = questionHandler;
	}

	@Override
	public boolean acceptMessage(Session session, Message message) {
		boolean accept = questionHandler.acceptMessage(session, message);
		if (!accept) return false;
		String text = message.text();
		for (Answer answer : session.getCurrentStepInformation().getAnswers()) {
			if (text.equalsIgnoreCase(answer.getAnswer())) return true;
		}
		return false;
	}

	@Override
	public Session handleMessage(Session session, Message message, TelegramBot telegramBot) {
		int answer = QuestionAnswerUtils.answerOrdinal(message.text(), session.getCurrentStepInformation());
		String question = session.getCurrentStepInformation().getQuestion();
		if (answer >= 0) session.answer(answer);
		logger.debug("{} [{}] Sending answer: {} {}", ChatProvider.TELEGRAM, message.chat().id(), question, answer);
		return session;
	}
}

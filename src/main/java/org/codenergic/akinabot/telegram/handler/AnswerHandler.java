package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QuestionAnswerUtils;
import org.codenergic.akinabot.telegram.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.Message;

@Service
@Order(Ordered.LOWEST_PRECEDENCE - 49)
class AnswerHandler implements QuestionAnswerHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Message message) {
		boolean accept = QuestionAnswerHandler.super.acceptMessage(session, message);
		if (!accept) return false;
		String text = message.text();
		for (Answer answer : session.getCurrentStepInformation().getAnswers()) {
			if (text.equalsIgnoreCase(answer.getAnswer())) return true;
		}
		return false;
	}

	@Override
	public void handleMessage(Session session, Message message, MessageHandlerChain chain) {
		int answer = QuestionAnswerUtils.answerOrdinal(message.text(), session.getCurrentStepInformation());
		String question = session.getCurrentStepInformation().getQuestion();
		if (answer >= 0) session.answer(answer);
		logger.debug("{} [{}] Sending answer: {} {}", ChatProvider.TELEGRAM, message.chat().id(), question, answer);
		chain.handleMessage(session, message);
	}
}

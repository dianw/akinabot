package org.codenergic.akinabot.line.handler;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QuestionAnswerUtils;
import org.codenergic.akinabot.line.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.event.Event;

@Service("lineAnswerHandler")
@Order(Ordered.LOWEST_PRECEDENCE - 49)
class AnswerHandler implements QuestionAnswerHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Event event) {
		boolean accept = QuestionAnswerHandler.super.acceptMessage(session, event);
		if (!accept) return false;
		String text = getMessageText(event).get();
		for (Answer answer : session.getCurrentStepInformation().getAnswers()) {
			if (text.equalsIgnoreCase(answer.getAnswer())) return true;
		}
		return false;
	}

	@Override
	public void handleMessage(Session session, Event event, MessageHandlerChain chain) {
		String text = getMessageText(event).get();
		int answer = QuestionAnswerUtils.answerOrdinal(text, session.getCurrentStepInformation());
		String question = session.getCurrentStepInformation().getQuestion();
		if (answer >= 0) session.answer(answer);
		logger.debug("{} [{}] Sending answer: {} {}", ChatProvider.LINE, event.getSource().getSenderId(), question, answer);
		chain.handleMessage(session, event);
	}
}

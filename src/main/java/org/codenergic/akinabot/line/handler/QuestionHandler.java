package org.codenergic.akinabot.line.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QuestionAnswerUtils;
import org.codenergic.akinabot.line.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Answer;
import org.codenergic.akinatorj.model.StepInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;

@Service("lineQuestionHandler")
class QuestionHandler implements QuestionAnswerHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleMessage(Session session, Event event, MessageHandlerChain chain) {
		StepInformation stepInformation = session.getCurrentStepInformation();
		String question = QuestionAnswerUtils.createQuestion(stepInformation);
		List<QuickReplyItem> quickReplyItems = stepInformation.getAnswers().stream()
				.map(Answer::getAnswer)
				.map(answer -> QuickReplyItem.builder()
						.action(PostbackAction.builder()
								.label(answer)
								.data(answer)
								.displayText(answer)
								.build())
						.build())
				.collect(Collectors.toList());
		TextMessage message = TextMessage.builder()
				.text(question)
				.quickReply(QuickReply.builder()
						.items(quickReplyItems)
						.item(QuickReplyItem.builder()
								.action(PostbackAction.builder()
										.label(AnswerButtons.QUIT.getText())
										.data(AnswerButtons.QUIT.getText())
										.displayText(AnswerButtons.QUIT.getText())
										.build())
								.build())
						.build())
				.build();
		logger.debug("{} [{}] Sending question: {}", ChatProvider.LINE, event.getSource().getSenderId(), stepInformation.getQuestion());
		chain.pushLineMessage(event.getSource().getSenderId(), message).thenRun(() -> chain.handleMessage(session, event));
	}
}

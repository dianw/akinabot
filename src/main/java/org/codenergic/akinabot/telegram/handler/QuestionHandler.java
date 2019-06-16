package org.codenergic.akinabot.telegram.handler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.QuestionAnswerUtils;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Answer;
import org.codenergic.akinatorj.model.StepInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Service
class QuestionHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Message message) {
		if (session == null) return false;
		int step = Integer.parseInt(session.getCurrentStepInformation().getStep());
		return message.text() != null && session.getProgression() < 90d && step < 35;
	}

	@Override
	public Session handleMessage(Session session, Message message, TelegramBot telegramBot) {
		StepInformation stepInformation = session.getCurrentStepInformation();
		String question = QuestionAnswerUtils.createQuestion(stepInformation);
		List<String> answers = stepInformation.getAnswers().stream()
				.map(Answer::getAnswer).collect(Collectors.toList());
		answers.add(AnswerButtons.QUIT.getText());
		AtomicInteger counter = new AtomicInteger();
		KeyboardButton[][] answerButtons = answers.stream()
				.map(KeyboardButton::new)
				.collect(Collectors.groupingBy(answer -> counter.getAndIncrement() / 3)).values()
				.stream()
				.map(buttons -> buttons.toArray(new KeyboardButton[0]))
				.toArray(KeyboardButton[][]::new);
		Keyboard keyboard = new ReplyKeyboardMarkup(answerButtons)
				.oneTimeKeyboard(true)
				.resizeKeyboard(true);
		telegramBot.execute(new SendMessage(message.chat().id(), question).replyMarkup(keyboard));
		logger.debug("{} [{}] Sending question: {}", ChatProvider.TELEGRAM, message.chat().id(), stepInformation.getQuestion());
		return session;
	}
}

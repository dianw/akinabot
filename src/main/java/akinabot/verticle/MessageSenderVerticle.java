package akinabot.verticle;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

import akinabot.Akinabot;
import akinabot.model.akinator.Elements;
import akinabot.model.akinator.StepInformation;
import akinabot.model.bot.QuestionAnswer;
import akinabot.service.QuestionAnswerUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class MessageSenderVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String TEXT_CANT_GUESS = "I'm sorry, I can't guess your character.";
	private static final String TEXT_GREETINGS = "Think about a real or fictional character , I will try to guess who it is.";
	private static final String TEXT_PROBLEM = "Sorry, we got a problem";
	private static final String TEXT_RESULT = "Your character is ";

	@Inject TelegramBot telegramBot;

	@Override
	public void start() throws Exception {
		EventBus eventBus = vertx.eventBus();
		
		eventBus.consumer(Akinabot.BUS_BOT_GREETINGS, this::sendGreetins);
		eventBus.consumer(Akinabot.BUS_BOT_IQUESTION, this::sendPreviousQuestion);
		eventBus.consumer(Akinabot.BUS_BOT_QUESTION, this::sendQuestion);
		eventBus.consumer(Akinabot.BUS_BOT_RESULT, this::sendResult);
		eventBus.consumer(Akinabot.BUS_BOT_SORRY, this::sendSorry);
	}

	private void sendGreetins(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();

		log.debug("[{}] Sending greeting message", qna.getChatId());

		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYNOW)
		}).oneTimeKeyboard(true);

		telegramBot.execute(new SendMessage(qna.getChatId(), TEXT_GREETINGS).replyMarkup(keyboard));
	}

	private void sendPreviousQuestion(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();

		log.debug("[{}] Invalid answer: {}, sending previous question", qna.getChatId(), qna.getAnswer().text());

		sendQuestions(qna.getChatId(), qna.getQuestion().getStepInformation());
	}

	private void sendQuestion(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();

		sendQuestions(qna.getChatId(), qna.getStepInformation());
	}

	private void sendQuestions(Long chatId, StepInformation stepInformation) {
		String question = QuestionAnswerUtils.createQuestion(stepInformation);

		log.debug("[{}] Sending question: {}", chatId, stepInformation.getQuestion());

		List<KeyboardButton> buttons = stepInformation.getAnswers().stream()
				.map(answer -> new KeyboardButton(answer.getAnswer())).collect(Collectors.toList());
		buttons.add(new KeyboardButton(Akinabot.BUTTON_QUIT));

		Keyboard keyboard = new ReplyKeyboardMarkup(buttons.toArray(new KeyboardButton[0])).oneTimeKeyboard(true)
				.resizeKeyboard(true);

		telegramBot.execute(new SendMessage(chatId, question).replyMarkup(keyboard));
	}

	private void sendResult(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();
		
		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYAGAIN)
		}).oneTimeKeyboard(true);

		List<Elements> answers = qna.getResult().getElements();
		if (answers.size() < 1) {
			telegramBot.execute(new SendMessage(qna.getChatId(), TEXT_CANT_GUESS).replyMarkup(keyboard));
			return;
		}

		Elements answer = answers.get(0);

		log.debug("[{}] RESULT: {}", qna.getChatId(), answer.getElement().getName());

		telegramBot.execute(new SendPhoto(qna.getChatId(), answer.getElement().getAbsolutePicturePath())
				.caption(TEXT_RESULT + answer.getElement().getName())
				.replyMarkup(keyboard));
	}

	private void sendSorry(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();
		
		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYAGAIN)
		}).oneTimeKeyboard(true);

		telegramBot.execute(new SendMessage(qna.getChatId(), TEXT_PROBLEM).replyMarkup(keyboard));
	}
}

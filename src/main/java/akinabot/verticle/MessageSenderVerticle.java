package akinabot.verticle;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

import akinabot.Akinabot;
import akinabot.model.akinator.Elements;
import akinabot.model.akinator.StepInformation;
import akinabot.model.bot.QuestionAnswer;
import akinabot.service.QuestionAnswerUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@Component
public class MessageSenderVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String TEXT_CANT_GUESS = "I'm sorry, I can't guess your character.";
	private static final String TEXT_GREETINGS = "Think about a real or fictional character , I will try to guess who it is.";
	private static final String TEXT_PROBLEM = "Sorry, we got a problem";
	private static final String TEXT_RESULT = "Your character is ";
	private static final String TEXT_TYPING = "typing";

	private TelegramBot telegramBot;

	@Inject
	public MessageSenderVerticle(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

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
		Long chatId = qna.getChatId();

		sendTypingAction(chatId);

		log.debug("[{}] Sending greeting message", chatId);

		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYNOW)
		}).oneTimeKeyboard(true);

		vertx.<SendResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendMessage(chatId, TEXT_GREETINGS).replyMarkup(keyboard)));
		}, response -> {
			SendResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Greeting message sent", chatId);
			} else {
				log.debug("[{}] Failed sending greeting message: ({}) {}", chatId,result.errorCode(), result.description());
			}
		});
		
	}

	private void sendTypingAction(Long chatId) {
		log.debug("[{}] Sending typing action message", chatId);

		vertx.<BaseResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendChatAction(chatId, TEXT_TYPING)));
		}, response -> {
			BaseResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Typing message sent", chatId);
			} else {
				log.debug("[{}] Failed sending typing message: ({}) {}", chatId,result.errorCode(), result.description());
			}
		});
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

		sendTypingAction(chatId);

		log.debug("[{}] Sending question: {}", chatId, stepInformation.getQuestion());

		List<KeyboardButton> buttons = stepInformation.getAnswers().stream()
				.map(answer -> new KeyboardButton(answer.getAnswer())).collect(Collectors.toList());
		buttons.add(new KeyboardButton(Akinabot.BUTTON_QUIT));

		Keyboard keyboard = new ReplyKeyboardMarkup(buttons.toArray(new KeyboardButton[0])).oneTimeKeyboard(true)
				.resizeKeyboard(true);

		vertx.<SendResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendMessage(chatId, question).replyMarkup(keyboard)));
		}, response -> {
			SendResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Question sent", chatId);
			} else {
				log.debug("[{}] Failed sending question: ({}) {}", chatId,result.errorCode(), result.description());
			}
		});
	}

	private void sendResult(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();
		Long chatId = qna.getChatId();

		sendTypingAction(chatId);

		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYAGAIN)
		}).oneTimeKeyboard(true);

		List<Elements> answers = qna.getResult().getElements();
		if (answers.size() < 1) {
			sendCantGuessResult(chatId, keyboard);
			return;
		}

		Elements answer = answers.get(0);

		log.debug("[{}] RESULT: {}", qna.getChatId(), answer.getElement().getName());

		vertx.<SendResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendPhoto(chatId, answer.getElement().getAbsolutePicturePath())
					.caption(TEXT_RESULT + answer.getElement().getName())
					.replyMarkup(keyboard)));
		}, response -> {
			SendResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Result sent", chatId);
			} else {
				log.debug("[{}] Failed sending result: ({}) {}", chatId,result.errorCode(), result.description());
			}
		});
	}

	private void sendCantGuessResult(Long chatId, Keyboard keyboard) {
		vertx.<SendResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendMessage(chatId, TEXT_CANT_GUESS).replyMarkup(keyboard)));
		}, response -> {
			SendResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Result sent", chatId);
			} else {
				log.debug("[{}] Failed sending result: ({}) {}", chatId, result.errorCode(), result.description());
			}
		});
	}

	private void sendSorry(Message<QuestionAnswer> msg) {
		QuestionAnswer qna = msg.body();
		Long chatId = qna.getChatId();

		sendTypingAction(chatId);

		Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[] {
				new KeyboardButton(Akinabot.BUTTON_PLAYAGAIN)
		}).oneTimeKeyboard(true);

		vertx.<SendResponse>executeBlocking(h -> {
			h.complete(telegramBot.execute(new SendMessage(chatId, TEXT_PROBLEM).replyMarkup(keyboard)));
		}, response -> {
			SendResponse result = response.result();
			if (result.isOk()) {
				log.debug("[{}] Result sent", chatId);
			} else {
				log.debug("[{}] Failed sending result: ({}) {}", chatId,result.errorCode(), result.description());
			}
		});
	}
}

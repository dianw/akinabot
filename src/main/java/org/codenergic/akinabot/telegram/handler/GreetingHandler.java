package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinatorj.Session;
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
class GreetingHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean acceptMessage(Session session, Message message) {
		return (message.text() != null && (message.text().equalsIgnoreCase(AnswerButtons.START.getText())
				|| message.text().equalsIgnoreCase(AnswerButtons.QUIT.getText())));
	}

	@Override
	public Session handleMessage(Session session, Message message, TelegramBot telegramBot) {
		KeyboardButton[] keyboardButtons = new KeyboardButton[]{
				new KeyboardButton(AnswerButtons.PLAY_NOW.getText())
		};
		Keyboard keyboard = new ReplyKeyboardMarkup(keyboardButtons).oneTimeKeyboard(true);
		telegramBot.execute(
				new SendMessage(message.chat().id(), Texts.GREETINGS.getText()).replyMarkup(keyboard));
		logger.debug("{} [{}] Sending greeting message", ChatProvider.TELEGRAM, message.chat().id());
		return session;
	}
}

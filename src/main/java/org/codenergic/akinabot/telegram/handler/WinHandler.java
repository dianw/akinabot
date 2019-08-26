package org.codenergic.akinabot.telegram.handler;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinabot.telegram.MessageHandlerChain;
import org.codenergic.akinatorj.Session;
import org.codenergic.akinatorj.model.Element;
import org.codenergic.akinatorj.model.ListParameters;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

@Service("telegramWinHandler")
class WinHandler implements MessageHandler {
	@Override
	public boolean acceptMessage(Session session, Message message) {
		if (session == null) return false;
		int step = Integer.parseInt(session.getCurrentStepInformation().getStep());
		return session.getProgression() >= 90d || step >= 35;
	}

	@Override
	public void handleMessage(Session session, Message message, MessageHandlerChain chain) {
		ListParameters parameters = session.win();
		if (parameters.getElements().isEmpty())
			chain.executeTelegramRequest(message.chat(), message.from(),
					new SendMessage(message.chat().id(), Texts.CANT_GUESS.getText()));
		Element element = parameters.getElements().get(0).getElement();
		SendPhoto sendPhoto = new SendPhoto(message.chat().id(), element.getAbsolutePicturePath())
				.caption(Texts.RESULT.getText() + element.getName() + " (" + element.getDescription() + ")");
		chain.executeTelegramRequest(message.chat(), message.from(), sendPhoto);
		KeyboardButton[] keyboardButtons = new KeyboardButton[]{
				new KeyboardButton(AnswerButtons.PLAY_AGAIN.getText())
		};
		Keyboard keyboard = new ReplyKeyboardMarkup(keyboardButtons).oneTimeKeyboard(true);
		chain.executeTelegramRequest(message.chat(), message.from(),
				new SendMessage(message.chat().id(), Texts.GREETINGS.getText()).replyMarkup(keyboard));
		chain.handleMessage(null, message);
	}
}

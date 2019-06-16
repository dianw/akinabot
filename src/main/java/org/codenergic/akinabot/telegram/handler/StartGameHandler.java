package org.codenergic.akinabot.telegram.handler;

import java.util.stream.Stream;

import org.codenergic.akinabot.core.AnswerButtons;
import org.codenergic.akinabot.core.Texts;
import org.codenergic.akinabot.telegram.MessageHandler;
import org.codenergic.akinatorj.AkinatorJ;
import org.codenergic.akinatorj.Session;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Service
@Order(Ordered.LOWEST_PRECEDENCE - 1)
class StartGameHandler implements MessageHandler {
	private final AkinatorJ akinatorJ;

	StartGameHandler(AkinatorJ akinatorJ) {
		this.akinatorJ = akinatorJ;
	}

	@Override
	public boolean acceptMessage(Session session, Message message) {
		String text = message.text();
		return text != null && (text.equalsIgnoreCase(AnswerButtons.PLAY_NOW.getText())
				|| text.equalsIgnoreCase(AnswerButtons.PLAY_AGAIN.getText()));
	}

	@Override
	public Session handleMessage(Session session, Message message, TelegramBot telegramBot) {
		Stream<Session> newSessions = Stream.of(akinatorJ.newSession("en"), akinatorJ.newSession("en2"));
		return newSessions
				.filter(s -> s.getCompletion().equalsIgnoreCase("OK"))
				.findFirst()
				.orElseGet(() -> {
					telegramBot.execute(new SendMessage(message.chat().id(), Texts.PROBLEM.getText()));
					return null;
				});
	}
}

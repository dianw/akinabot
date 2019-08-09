package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.Map;

import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public class MessageHandlerChain {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerChain.class);

	private final TelegramBot telegramBot;
	private final Map<Long, Session> sessions;
	private final List<MessageHandler> messageHandlers;
	private int chainPosition = 0;
	private Session currentSession;

	MessageHandlerChain(TelegramBot telegramBot, Map<Long, Session> sessions, List<MessageHandler> messageHandlers) {
		this.telegramBot = telegramBot;
		this.sessions = sessions;
		this.messageHandlers = messageHandlers;
	}

	public void handleMessage(Session session, Message message) {
		if (chainPosition == messageHandlers.size()) return;
		MessageHandler messageHandler = messageHandlers.get(chainPosition++);
		if (session == null) {
			sessions.remove(message.chat().id());
		} else if (!session.equals(currentSession)) {
			sessions.put(message.chat().id(), session);
		}
		if (messageHandler.acceptMessage(session, message)) {
			LOGGER.debug("Executing handler: {}", messageHandler);
			messageHandler.handleMessage(session, message, this);
			currentSession = session;
		} else {
			handleMessage(session, message);
		}
	}

	public TelegramBot getTelegramBot() {
		return telegramBot;
	}
}

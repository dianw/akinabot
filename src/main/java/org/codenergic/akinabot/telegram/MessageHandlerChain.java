package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.Map;

import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

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

	public <T extends BaseRequest, R extends BaseResponse> R executeTelegramRequest(Chat chat, User user, BaseRequest<T, R> request) {
		return telegramBot.execute(request);
	}

	public <T extends BaseRequest<T, R>, R extends BaseResponse> void executeTelegramRequest(Chat chat, User user, T request, Callback<T, R> callback) {
		telegramBot.execute(request, callback);
	}

	public void handleMessage(Session session, Message message) {
		if (chainPosition == messageHandlers.size()) return;
		MessageHandler messageHandler = messageHandlers.get(chainPosition++);
		if (session == null) {
			sessions.remove(message.chat().id());
		} else if (currentSession == null ||
				!session.getNewSessionResponse().equals(currentSession.getNewSessionResponse())) {
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

}

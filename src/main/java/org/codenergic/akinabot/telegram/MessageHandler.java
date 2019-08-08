package org.codenergic.akinabot.telegram;

import org.codenergic.akinatorj.Session;

import com.pengrad.telegrambot.model.Message;

public interface MessageHandler {
	boolean acceptMessage(Session session, Message message);

	void handleMessage(Session session, Message message, MessageHandlerChain chain);
}

package org.codenergic.akinabot.core;

import java.io.Serializable;
import java.util.Optional;

public class MessageEvent implements Serializable {
	private final ChatProvider chatProvider;
	private final InOut inOut;
	private final String chatId;
	private final String username;

	public MessageEvent(ChatProvider chatProvider, InOut inOut, String chatId, String username) {
		this.chatProvider = chatProvider;
		this.inOut = inOut;
		this.chatId = chatId;
		this.username = Optional.ofNullable(username).orElse(chatId);
	}

	String getChatId() {
		return chatId;
	}

	ChatProvider getChatProvider() {
		return chatProvider;
	}

	InOut getInOut() {
		return inOut;
	}

	String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "MessageEvent{" +
				"chatProvider=" + chatProvider +
				", inOut=" + inOut +
				", chatId='" + chatId + '\'' +
				", username='" + username + '\'' +
				'}';
	}

	public enum InOut {
		INBOUND, OUTBOUND
	}
}

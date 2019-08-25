package org.codenergic.akinabot.core;

public class MessageEvent {
	private final ChatProvider chatProvider;
	private final InOut inOut;
	private final String chatId;
	private final String username;

	public MessageEvent(ChatProvider chatProvider, InOut inOut, String chatId, String username) {
		this.chatProvider = chatProvider;
		this.inOut = inOut;
		this.chatId = chatId;
		this.username = username;
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

	public enum InOut {
		INBOUND, OUTBOUND
	}
}

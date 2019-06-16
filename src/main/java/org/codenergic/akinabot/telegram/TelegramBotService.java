package org.codenergic.akinabot.telegram;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;

@Service
class TelegramBotService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final TelegramBot telegramBot;
	private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
	private final List<MessageHandler> messageHandlers;

	TelegramBotService(TelegramBot telegramBot, List<MessageHandler> messageHandlers) {
		logger.info("Initializing bot service, available message handlers: {}", messageHandlers);
		this.telegramBot = telegramBot;
		this.messageHandlers = messageHandlers;
	}

	void onUpdate(Update update) {
		Message message = update.message();
		if (message == null) return;
		Long chatId = message.chat().id();
		messageHandlers.stream()
				.filter(handler -> handler.acceptMessage(sessions.get(chatId), message))
				.peek(handler -> logger.debug("{} [{}] Found message handler: {}", ChatProvider.TELEGRAM, chatId, handler))
				.peek(handler -> sendTypingAction(chatId))
				.map(handler -> handler.handleMessage(sessions.get(chatId), message, telegramBot))
				.filter(Objects::nonNull)
				.forEach(session -> sessions.put(chatId, session));
	}

	private void sendTypingAction(Long chatId) {
		telegramBot.execute(new SendChatAction(chatId, ChatAction.typing));
		logger.debug("{} [{}] Sending typing action message", ChatProvider.TELEGRAM, chatId);
	}
}

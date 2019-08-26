package org.codenergic.akinabot.line;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.codenergic.akinabot.core.ChatProvider;
import org.codenergic.akinabot.core.MessageEvent;
import org.codenergic.akinatorj.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.response.BotApiResponse;

public class MessageHandlerChain {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerChain.class);

	private final LineMessagingClient lineMessagingClient;
	private final Map<Source, Session> sessions;
	private final List<MessageHandler> messageHandlers;
	private final Consumer<MessageEvent> messageLogger;
	private int chainPosition = 0;
	private Session currentSession;

	MessageHandlerChain(LineMessagingClient lineMessagingClient, Map<Source, Session> sessions, List<MessageHandler> messageHandlers, Consumer<MessageEvent> messageLogger) {
		this.lineMessagingClient = lineMessagingClient;
		this.sessions = sessions;
		this.messageHandlers = messageHandlers;
		this.messageLogger = messageLogger;
	}

	public void handleMessage(Session session, Event event) {
		if (chainPosition == messageHandlers.size()) return;
		MessageHandler messageHandler = messageHandlers.get(chainPosition++);
		if (session == null) {
			sessions.remove(event.getSource());
		} else if (currentSession == null ||
				!session.getNewSessionResponse().equals(currentSession.getNewSessionResponse())) {
			sessions.put(event.getSource(), session);
		}
		if (messageHandler.acceptMessage(session, event)) {
			LOGGER.debug("Executing handler: {}", messageHandler);
			messageHandler.handleMessage(session, event, this);
			currentSession = session;
		} else {
			handleMessage(session, event);
		}
	}

	private void publishOutboundMessageEvent(String senderId) {
		messageLogger.accept(new MessageEvent(ChatProvider.LINE, MessageEvent.InOut.OUTBOUND, senderId, senderId));
	}

	public CompletableFuture<BotApiResponse> pushLineMessage(String senderId, Message... messages) {
		publishOutboundMessageEvent(senderId);
		return lineMessagingClient.pushMessage(new PushMessage(senderId, Arrays.asList(messages)));
	}
}

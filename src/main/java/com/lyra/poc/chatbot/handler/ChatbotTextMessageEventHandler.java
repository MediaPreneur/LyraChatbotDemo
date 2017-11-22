package com.lyra.poc.chatbot.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.receive.events.TextMessageEvent;
import com.github.messenger4j.receive.handlers.TextMessageEventHandler;
import com.lyra.poc.chatbot.controller.ApiAIResponseHandler;

import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

@Component
public class ChatbotTextMessageEventHandler implements TextMessageEventHandler {

	private static final Logger logger = LoggerFactory.getLogger("webhook");

	@Autowired
	private AIDataService dataService;

	@Autowired
	private ApiAIResponseHandler apiAiResponseHandler;

	@Override
	public void handle(TextMessageEvent event) {
		logger.debug("Received TextMessageEvent: {}", event);

		final String messageId = event.getMid();
		final String messageText = event.getText();
		final String senderId = event.getSender().getId();
		final Date timestamp = event.getTimestamp();

		logger.info("Received message '{}' with text '{}' from user '{}' at '{}'", messageId, messageText, senderId,
				timestamp);

		handleApiAiInteraction(messageText, senderId);
	}

	/**
	 * Forwards the message to api.ai and executes actions according to the
	 * resolved intent
	 * 
	 * @param messageText
	 * @param recipientId
	 */
	private void handleApiAiInteraction(String messageText, String recipientId) {
		try {
			AIRequest request = new AIRequest(messageText);
			AIResponse response = dataService.request(request);

			if (response.getStatus().getCode() == 200) {
				apiAiResponseHandler.handle(response, recipientId);
			} else {
				System.err.println(response.getStatus().getErrorDetails());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}

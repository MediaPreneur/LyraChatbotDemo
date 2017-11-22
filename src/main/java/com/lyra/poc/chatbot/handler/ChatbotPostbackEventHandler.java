package com.lyra.poc.chatbot.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.receive.events.PostbackEvent;
import com.github.messenger4j.receive.handlers.PostbackEventHandler;
import com.lyra.poc.chatbot.controller.ApiAIResponseHandler;

import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

@Component
public class ChatbotPostbackEventHandler implements PostbackEventHandler {

	private static final String API_AI_GETSTARTED = "Bonjour";

	private static final Logger logger = LoggerFactory.getLogger("webhook");
	
	public static final String GET_STARTED_PAYLOAD ="GET_STARTED_PAYLOAD";
	
	@Autowired
	private AIDataService dataService;

	@Autowired
	private ApiAIResponseHandler apiAiResponseHandler;
	
	@Override
	public void handle(PostbackEvent event) {
		logger.debug("Received PostbackEvent: {}", event);

		final String payload = event.getPayload();
		final String senderId = event.getSender().getId();
		final Date timestamp = event.getTimestamp();

		logger.info("Received postback with payload '{}' from user '{}' at '{}'", payload, senderId,
				timestamp);

		//Behind the scenes, send "hello" so as to API.AI to trigger conversation start
		handleApiAiInteraction(senderId);
	}
	
	/**
	 * Forwards the message to api.ai and executes actions according to the
	 * resolved intent
	 * 
	 * @param messageText
	 * @param recipientId
	 */
	private void handleApiAiInteraction(String recipientId) {
		try {
			AIRequest request = new AIRequest(API_AI_GETSTARTED);
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

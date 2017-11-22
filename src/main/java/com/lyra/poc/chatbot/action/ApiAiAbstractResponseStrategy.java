package com.lyra.poc.chatbot.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.QuickReply;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.GenericTemplate;
import com.github.messenger4j.send.templates.GenericTemplate.Element.ListBuilder;
import com.lyra.poc.chatbot.model.ConversationContext;

import ai.api.model.AIResponse;

public abstract class ApiAiAbstractResponseStrategy implements ApiAIResponseStrategy{

	private static final Logger logger = LoggerFactory.getLogger("webhook");
	
	@Autowired
    protected MessengerSendClient sendClient;
	
	public void echoApiAISpeech(AIResponse response, ConversationContext context) throws MessengerApiException, MessengerIOException {
		sendClient.sendTextMessage(context.getFacebookId(), response.getResult().getFulfillment().getSpeech());
	}
	
	public void showQuickReplyCarousel(String apiAiSpeech, ConversationContext context) throws MessengerApiException, MessengerIOException {
		logger.debug("Showing carousel");
		String[] paymentMethods = new String[]{"CB", "Visa", "Maestro"};
		
		com.github.messenger4j.send.QuickReply.ListBuilder newListBuilder = QuickReply.newListBuilder();
		for(String paymentMethod : paymentMethods) {
            String paymentMethodLowerCase = paymentMethod.toLowerCase();
            newListBuilder = newListBuilder
            		.addTextQuickReply(paymentMethod, "{'typeCarte': '"+paymentMethod+"'}")
            		.imageUrl("https://chatbot-demo.lyra-labs.fr/img/"+paymentMethodLowerCase+"_logo.png")
            		.toList();
		}
		
		List<QuickReply> quickReplies = newListBuilder.build();
		logger.debug("Quick replies built");
		sendClient.sendTextMessage(context.getFacebookId(), apiAiSpeech, quickReplies);
		logger.debug("Quick replies sent to facebook ID {}", context.getFacebookId());
	}
	
}

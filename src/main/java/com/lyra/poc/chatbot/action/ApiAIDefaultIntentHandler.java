package com.lyra.poc.chatbot.action;

import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.lyra.poc.chatbot.model.ConversationContext;

import ai.api.model.AIResponse;

/**
 * Handles any remaining intents where the message should be returned as-is
 * Ex: small talk, unknown intent...
 * @author proland
 *
 */
@Component
public class ApiAIDefaultIntentHandler extends ApiAiAbstractResponseStrategy{
    
    @Override
    public void execute(AIResponse response, ConversationContext context) throws MessengerApiException, MessengerIOException {
    	echoApiAISpeech(response, context);
    }

}

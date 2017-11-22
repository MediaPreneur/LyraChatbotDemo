package com.lyra.poc.chatbot.action;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.lyra.poc.chatbot.model.ConversationContext;

import ai.api.model.AIResponse;

/**
 * Performs specific actions for an api.ai response
 * @author mregragui
 *
 */
public interface ApiAIResponseStrategy {

    void execute(AIResponse response, ConversationContext context) throws MessengerApiException, MessengerIOException;
    
}

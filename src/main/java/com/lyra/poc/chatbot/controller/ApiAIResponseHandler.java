package com.lyra.poc.chatbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.lyra.poc.chatbot.action.ApiAICheckoutHandler;
import com.lyra.poc.chatbot.action.ApiAIDefaultIntentHandler;
import com.lyra.poc.chatbot.action.ApiAIEmailHandler;
import com.lyra.poc.chatbot.action.ApiAINoEmailHandler;
import com.lyra.poc.chatbot.action.ApiAIResponseStrategy;
import com.lyra.poc.chatbot.model.ConversationContext;
import com.lyra.poc.chatbot.technical.ConversationContextHolder;

import ai.api.model.AIResponse;

/**
 * Chooses a strategy to execute based on api.ai response and executes it
 * @author mregragui
 *
 */
@Component
public class ApiAIResponseHandler {

	private static final Logger logger = LoggerFactory.getLogger("webhook");
	
    @Autowired
    private ConversationContextHolder ctxHolder;
    
    @Autowired
    private ApiAICheckoutHandler checkoutHandler;
    
    @Autowired
    private ApiAIDefaultIntentHandler defaultIntentHandler;
    
    @Autowired
    private ApiAIEmailHandler emailHandler;
    
    @Autowired
    private ApiAINoEmailHandler noEmailHandler;
    
    public void handle(AIResponse response, String recipientId) throws MessengerApiException, MessengerIOException {
        String intent = response.getResult().getAction();
        
        ApiAIResponseStrategy strategy = getStrategyForIntent(intent);
        logger.debug("Assigned intent '{}' (with speech '{}') to strategy handler {}", intent, response.getResult().getFulfillment().getSpeech(), strategy.getClass().getSimpleName());
        
        ConversationContext context = ctxHolder.getContext(recipientId);

        strategy.execute(response, context);
        
    }
    
    private ApiAIResponseStrategy getStrategyForIntent(String intent) {
        switch(intent) {
            case "email" : return emailHandler;
            case "payment" : return checkoutHandler;
            case "email.none" : return noEmailHandler;
            default : return defaultIntentHandler;
        }
    }
    
}

package com.lyra.poc.chatbot.action;

import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.lyra.poc.chatbot.model.ConversationContext;

import ai.api.model.AIResponse;

@Component
public class ApiAINoEmailHandler extends ApiAiAbstractResponseStrategy{

    @Override
    public void execute(AIResponse response, ConversationContext context)
            throws MessengerApiException, MessengerIOException {
        
        context.setEmail(null);
        showQuickReplyCarousel(response.getResult().getFulfillment().getSpeech(), context);
    }

}

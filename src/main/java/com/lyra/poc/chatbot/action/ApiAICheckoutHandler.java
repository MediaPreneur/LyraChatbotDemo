package com.lyra.poc.chatbot.action;

import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.google.gson.JsonElement;
import com.lyra.poc.chatbot.model.ConversationContext;

import ai.api.model.AIResponse;

/**
 * When the user wants to pay a specific amount
 * Also handled cases where a user states he wants to pay, but now how much (API.AI will ask for additional information)
 * e.g. "Je souhaiterais payer 10€"
 * @author mregragui
 *
 */
@Component
public class ApiAICheckoutHandler extends ApiAiAbstractResponseStrategy{

	@Override
    public void execute(AIResponse response, ConversationContext context) {
    	

    	
        JsonElement amountNode = response.getResult().getParameters().get("amount");
        JsonElement centAmountNode = response.getResult().getParameters().get("centAmount");

        //API.AI replaces amounts with ',' with '.'. Ex: 54,12 -> 54.12
        //Cents can either come in this form or separately, if the user asks '5 euros et 12 centimes', for instance
        try {
            if(amountNode!=null) {
                Double amountDouble = amountNode.getAsDouble() * 100;
                Integer amount = amountDouble.intValue();
                if(centAmountNode != null) {
                	Integer centAmount = centAmountNode.getAsInt();
                	amount = amount + centAmount;
                }
                context.setAmount(amount);
            }
            echoApiAISpeech(response, context);
        } catch (MessengerApiException | MessengerIOException e) {
            throw new RuntimeException(e);
        }

    }

}

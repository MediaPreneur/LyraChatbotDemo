package com.lyra.poc.chatbot.action;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.GenericTemplate;
import com.github.messenger4j.send.templates.GenericTemplate.Element.ListBuilder;
import com.lyra.poc.chatbot.model.ConversationContext;
import com.lyra.poc.chatbot.technical.PayzenOrderGenerator;

/**
 * Handles generating an order with the context and sends the link to the customer
 * @author mregragui
 *
 */
@Component
public class PaymentStrategy {

    @Autowired
    private MessengerSendClient sendClient;
    
    @Autowired
    private PayzenOrderGenerator orderGenerator;
    
    /**
     * Generates an order with the information in the context, sends back the url for the order,
     * and resets the amount in the context
     * @param context
     */
    public void generateAndSendOrder(ConversationContext context, String cardType) {

    	Integer amount = context.getAmount();
		if(amount == null) {
			try {
				sendClient.sendTextMessage(context.getFacebookId(), "Excusez-moi, je n'ai plus le montant en tête. Combien souhaitez-vous payer?");
				return;
			} catch (MessengerApiException | MessengerIOException e) {
	            throw new RuntimeException(e);
	        }
    	}
    	
    	String email = context.getEmail().orElse("trash@lyra-network.com");
        String url = orderGenerator.generateOrder(amount, email, cardType.toUpperCase(), context.getFacebookId(), context.getUserProfile().getFirstName(), context.getUserProfile().getLastName());
        
        try {
        	//divide by 100 to get amount to show end user
        	BigDecimal decimal = new BigDecimal(amount);
        	decimal = decimal.divide(new BigDecimal(100));
        	
    		ListBuilder genericTemplateBuilder = GenericTemplate.newBuilder().addElements();
            List<Button> button = Button.newListBuilder()
                    .addUrlButton("Payer " + decimal + "€", url)
                    .messengerExtensions(true)
                    .toList()
                    .build();
            genericTemplateBuilder = genericTemplateBuilder
                    .addElement("Paiement de TEST")
                    .imageUrl("https://chatbot-demo.lyra-labs.fr/img/logo-votre-boutique-horizontal.png")
                    .buttons(button)
                    .toList();
            
            GenericTemplate genericTemplate = genericTemplateBuilder.done().build();
            sendClient.sendTemplate(context.getFacebookId(), genericTemplate);

        } catch (MessengerApiException | MessengerIOException e) {
            throw new RuntimeException(e);
        }
        
        context.setAmount(null);
    }
    
}

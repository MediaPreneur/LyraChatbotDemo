package com.lyra.poc.chatbot.handler;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.receive.events.QuickReplyMessageEvent;
import com.github.messenger4j.receive.handlers.QuickReplyMessageEventHandler;
import com.lyra.poc.chatbot.action.PaymentStrategy;
import com.lyra.poc.chatbot.model.ConversationContext;
import com.lyra.poc.chatbot.technical.ConversationContextHolder;

@Component
public class ChatbotQuickReplyEventHandler implements QuickReplyMessageEventHandler {

	private static final Logger logger = LoggerFactory.getLogger("webhook");
	
	@Autowired
    private PaymentStrategy paymentStrategy;
    
    @Autowired
    private ConversationContextHolder ctxHolder;
	
	private static final Set<String> allCardTypes;
    static {
    	allCardTypes = new HashSet<String>();
    	allCardTypes.add("CB");
    	allCardTypes.add("Visa");
    	allCardTypes.add("Visa Electron");
    	allCardTypes.add("Mastercard");
    	allCardTypes.add("Maestro");
    	allCardTypes.add("Bancontact Mistercash");
    }
	
	@Override
	public void handle(QuickReplyMessageEvent event) {
		
		 logger.debug("Received Quick Reply event: {}", event);

	        final String messageId = event.getMid();
	        final String messageText = event.getText();
	        final String senderId = event.getSender().getId();
	        final Date timestamp = event.getTimestamp();

	        logger.info("Received quick reply '{}' with text '{}' from user '{}' at '{}'",
	                messageId, messageText, senderId, timestamp);
		
		Optional<String> findFirst = allCardTypes.stream().filter(messageText::contains).findFirst();
		if(findFirst.isPresent()) {
        	String cardType = findFirst.get();
			logger.info("Card Type detected: " + cardType);
        	ConversationContext context;
			try {
				context = ctxHolder.getContext(senderId);
				paymentStrategy.generateAndSendOrder(context, cardType);
			} catch (MessengerApiException | MessengerIOException e) {
				logger.error("Could not handle quick reply: ", e);
			}
        }
	}

}

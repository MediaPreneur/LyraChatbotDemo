package com.lyra.poc.chatbot.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;

@Controller
public class RedirectionController {

	private static final Logger logger = LoggerFactory.getLogger("app");
	
	@Autowired
	private MessengerSendClient sendClient;

	@RequestMapping("/redirection")
	public String redirection(@RequestParam(value = "facebookId", required = false) String facebookId,
			@RequestParam(value = "paymentStatus", required = true) String paymentStatus) {    
		
		logger.info("Received parameters to send facebook message : \n\t- facebookId : " + (facebookId!=null?facebookId:"NOT_PRESENT")
				+ "\n\t- paymentStatus : " + paymentStatus);
		
		// Set object to redirect to Messenger
		
		
		String message;

		switch (paymentStatus) {
		case "cancel":
			message = "Votre paiement a été annulé";
			break;

		case "refused":
			message = "Désolé, votre paiement a été refusé";
			break;

		case "success":
			message = "Votre paiement a bien été accepté";
			break;
			
		case "expired":
			message = "Votre paiement a expiré";
			break;

		default:
			message = "Votre paiement est dans le statut " + paymentStatus;
		}

		try {
			if(facebookId != null) {
				sendClient.sendTextMessage(facebookId, message);
			}
        } catch (MessengerApiException | MessengerIOException e) {
            throw new RuntimeException(e);
        }
		

		return "closeMessenger";
	}

}

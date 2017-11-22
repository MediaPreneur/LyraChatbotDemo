package com.lyra.poc.chatbot.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.messenger4j.receive.events.Event;
import com.github.messenger4j.receive.events.PostbackEvent;
import com.github.messenger4j.receive.events.TextMessageEvent;

/**
 * Aspect permettant de gérer les erreurs des services.
 * Actuellement, on logue l'erreur et on quitte avec un code d'erreur.
 * @author mregragui
 *
 */
@Component
@Aspect
public class ServiceErrorAspect {
	
	private Logger errorLogger = LoggerFactory.getLogger("error");
	private Logger logger = LoggerFactory.getLogger("webhook");
	
	
	@Before("textMessageHandler()")
	public void logMessage(JoinPoint jp){
	    Event event = getEvent(jp);
	    
	    logger.debug("Received by " + event.getRecipient().getId());
		
	}
	
	@AfterThrowing(pointcut="textMessageHandler()", throwing="e")
	public void doIt(JoinPoint jp, Exception e){
	    
	    Event event = getEvent(jp);
	    String recipient = event.getRecipient().getId();
	    String sender = event.getSender().getId();
	    
	    String text;
	    if(TextMessageEvent.class.isAssignableFrom(event.getClass())){
	    	text = ((TextMessageEvent) event).getText();
	    }else if(PostbackEvent.class.isAssignableFrom(event.getClass())){
	    	text = ((PostbackEvent) event).getPayload();
	    }
	    else{
	    	text = "Not a text message";
	    }
	    
	    errorLogger.error("Error while processing message sent by " + sender + " | received by " + recipient + " | " + text, e);
	    
	}

	private Event getEvent(JoinPoint jp) {
		Object[] args = jp.getArgs();
	    Event event = (Event) args[0];
		return event;
	}
	
	@Pointcut("execution(* com.lyra.poc.chatbot.handler..*.*(..))")
	private void textMessageHandler(){
	}

}

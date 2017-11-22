package com.lyra.poc.chatbot;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.handlers.FallbackEventHandler;
import com.github.messenger4j.receive.handlers.PostbackEventHandler;
import com.github.messenger4j.receive.handlers.QuickReplyMessageEventHandler;
import com.github.messenger4j.receive.handlers.TextMessageEventHandler;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.MessengerSendClientBuilder;

@Configuration
@Profile("prod")
public class MessengerConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger("webhook");
    
    @Autowired
    private ProxyCapableMessengerHttpClient proxyCapableMessengerHttpClient;
    
    /**
     * Initializes the {@code MessengerSendClient}.
     *
     * @param pageAccessToken the generated {@code Page Access Token}
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Bean
    public MessengerSendClient messengerSendClient(@Value("${messenger4j.pageAccessToken}") String pageAccessToken) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        logger.debug("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        
        MessengerSendClientBuilder newSendClientBuilder = MessengerPlatform.newSendClientBuilder(pageAccessToken);
        //XXX HACK: use reflection to make httpClient visible, and fill in with our proxy-capable implementation. 
        //The only alternative is to create an entirely new implementation for MessengerSendClient
        //This is done because the builder does not allow giving both a pageAccessToken and a MessengerHttpClient
        Class<?> builderClass = newSendClientBuilder.getClass();
        Field httpClientField = builderClass.getDeclaredField("httpClient");
		httpClientField.setAccessible(true);
		httpClientField.set(newSendClientBuilder, proxyCapableMessengerHttpClient);
        
		return newSendClientBuilder.build();
    }
    
    @Bean
    public MessengerReceiveClient messengerReceiveClient(@Value("${messenger4j.appSecret}") final String appSecret,
            @Value("${messenger4j.verifyToken}") final String verifyToken,
            TextMessageEventHandler textMessageHandler,
            QuickReplyMessageEventHandler quickReplyHandler,
            PostbackEventHandler postbackHandler) {
        return  MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
                .onTextMessageEvent(textMessageHandler)
                .onQuickReplyMessageEvent(quickReplyHandler)
                .onPostbackEvent(postbackHandler)
                .fallbackEventHandler(newFallbackEventHandler())
                .build();
    }
    
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }

}

package com.lyra.poc.chatbot.technical;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.user.UserProfile;
import com.github.messenger4j.user.UserProfileClient;
import com.github.messenger4j.user.UserProfileClientBuilder;
import com.lyra.poc.chatbot.ProxyCapableMessengerHttpClient;
import com.lyra.poc.chatbot.model.ConversationContext;

/**
 * Cache of conversation contexts
 * @author mregragui
 *
 */
@Component
public class ConversationContextHolder {

    private static final int SIZE_LIMIT = 100;

    private Map<String, ConversationContext> contextsMap = new HashMap<>();
    
    @Value("${messenger4j.pageAccessToken}") 
    private String pageAccessToken;

    @Autowired
    private ProxyCapableMessengerHttpClient proxyCapableMessengerHttpClient;
    
    private static final Logger logger = LoggerFactory.getLogger("webhook");
    
    private Queue<String> contextIdsQueue = new LinkedList<>();

    /**
     * If a conversation context exists for the specified facebook id, it is returned.
     * Otherwise, a new empty context is created, registered in the cache, and returned
     * @param facebookId
     * @return
     * @throws MessengerIOException 
     * @throws MessengerApiException 
     */
    public ConversationContext getContext(String facebookId) throws MessengerApiException, MessengerIOException {
        ConversationContext ctx = contextsMap.get(facebookId);
        if(ctx==null) {
            return registerContext(facebookId);
        }else {
            return ctx;
        }
    }

    /**
     * Pre-requisiste : no other context with the facebook id currently in the Map
     * @param facebookId
     * @return
     * @throws MessengerIOException 
     * @throws MessengerApiException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private ConversationContext registerContext(String facebookId) {
    	UserProfileClientBuilder newUserProfileClientBuilder = MessengerPlatform.newUserProfileClientBuilder(pageAccessToken);
    	//XXX HACK: use reflection to make httpClient visible, and fill in with our proxy-capable implementation. 
    	//The only alternative is to create an entirely new implementation for UserProfileClientBuilder
    	//This is done because the builder does not allow giving both a pageAccessToken and a MessengerHttpClient
    	try{
    		Class<?> builderClass = newUserProfileClientBuilder.getClass();
    		Field httpClientField = builderClass.getDeclaredField("httpClient");
    		httpClientField.setAccessible(true);
    		httpClientField.set(newUserProfileClientBuilder, proxyCapableMessengerHttpClient);
    		UserProfileClient userProfileClient = newUserProfileClientBuilder.build();
    		UserProfile userProfile = userProfileClient.queryUserProfile(facebookId);
    		ConversationContext ctx = new ConversationContext();
    		ctx.setFacebookId(facebookId);
    		ctx.setUserProfile(userProfile);
    		logger.debug("Successfully retrieved FB profile for user {} {}", userProfile.getFirstName(), userProfile.getLastName() );
    		
    		contextsMap.put(facebookId, ctx);
    		contextIdsQueue.add(facebookId);
    		
    		cleanUp();
    		
    		return ctx;
    	}
    	catch(Exception e) {
    		logger.error("Could not retrieve facebook context", e);
    	}
    
    	return null;
    }

    private void cleanUp() {
        if(contextIdsQueue.size() > SIZE_LIMIT) {
            String first = contextIdsQueue.poll();
            contextsMap.remove(first);
        }
    }


}

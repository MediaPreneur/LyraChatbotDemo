package com.lyra.poc.chatbot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.handlers.FallbackEventHandler;
import com.github.messenger4j.receive.handlers.PostbackEventHandler;
import com.github.messenger4j.receive.handlers.QuickReplyMessageEventHandler;
import com.github.messenger4j.receive.handlers.TextMessageEventHandler;
import com.github.messenger4j.send.BinaryAttachment;
import com.github.messenger4j.send.MessengerResponse;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.QuickReply;
import com.github.messenger4j.send.Recipient;
import com.github.messenger4j.send.SenderAction;
import com.github.messenger4j.send.templates.Template;

@Configuration
@Profile("mock")
public class MessengerMockConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger("webhook");
    private static final Logger loggerMock = LoggerFactory.getLogger("mock");
    
    /**
     * Initializes the {@code MessengerSendClient}.
     *
     * @param pageAccessToken the generated {@code Page Access Token}
     */
    @Bean
    public MessengerSendClient messengerSendClient(@Value("${messenger4j.pageAccessToken}") String pageAccessToken) {
        return new MessengerSendClient() {
            
            @Override
            public MessengerResponse sendVideoAttachment(String recipientId, String videoUrl)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(Recipient recipient, NotificationType notificationType, String text,
                    List<QuickReply> quickReplies, String metadata) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(Recipient recipient, NotificationType notificationType, String text, String metadata)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(Recipient recipient, NotificationType notificationType, String text,
                    List<QuickReply> quickReplies) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(Recipient recipient, NotificationType notificationType, String text)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(String recipientId, String text, List<QuickReply> quickReplies)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTextMessage(String recipientId, String text) throws MessengerApiException, MessengerIOException {
                loggerMock.info("Message envoyé à " + recipientId + " : "  + text);
                return null;
            }
            
            @Override
            public MessengerResponse sendTemplate(Recipient recipient, NotificationType notificationType, Template template,
                    List<QuickReply> quickReplies, String metadata) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTemplate(Recipient recipient, NotificationType notificationType, Template template,
                    String metadata) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTemplate(Recipient recipient, NotificationType notificationType, Template template,
                    List<QuickReply> quickReplies) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTemplate(Recipient recipient, NotificationType notificationType, Template template)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendTemplate(String recipientId, Template template)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendSenderAction(Recipient recipient, NotificationType notificationType, SenderAction senderAction)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendSenderAction(String recipientId, SenderAction senderAction)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendImageAttachment(String recipientId, String imageUrl, List<QuickReply> quickReplies)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendImageAttachment(String recipientId, String imageUrl)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendFileAttachment(String recipientId, String fileUrl)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendBinaryAttachment(Recipient recipient, NotificationType notificationType,
                    BinaryAttachment binaryAttachment, List<QuickReply> quickReplies, String metadata)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendBinaryAttachment(Recipient recipient, NotificationType notificationType,
                    BinaryAttachment binaryAttachment, String metadata) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendBinaryAttachment(Recipient recipient, NotificationType notificationType,
                    BinaryAttachment binaryAttachment, List<QuickReply> quickReplies) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendBinaryAttachment(Recipient recipient, NotificationType notificationType,
                    BinaryAttachment binaryAttachment) throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendBinaryAttachment(String recipientId, BinaryAttachment binaryAttachment)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MessengerResponse sendAudioAttachment(String recipientId, String audioUrl)
                    throws MessengerApiException, MessengerIOException {
                // TODO Auto-generated method stub
                return null;
            }
        };
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
                .disableSignatureVerification()
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

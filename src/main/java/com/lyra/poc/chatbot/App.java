package com.lyra.poc.chatbot;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.lyra.poc.chatbot.technical.ChatbotProxyHandler;
import com.lyra.poc.chatbot.technical.RestRequestLogger;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import okhttp3.OkHttpClient;

@SpringBootApplication
@Import({MessengerConfiguration.class, MessengerMockConfiguration.class})
@EnableAspectJAutoProxy
public class App {
	
	private static final Logger logger = LoggerFactory.getLogger("app");

	@Autowired
	private RestRequestLogger restLogger;

	@Autowired
	private ChatbotProxyHandler proxyHandler;
	
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("démarré");
    }
    
    /**
     * Initializes the {@code AIDataService}.
     *
     * @param apiKey the API key
     */
    @Bean 
    public AIDataService aIDataService(@Value("${apiai.apiKey}") String apiKey) {
    	logger.debug("Initializing AIDataService - api Key: {}", apiKey);
    	AIConfiguration config = new AIConfiguration(apiKey);
    		
    	Proxy proxy = proxyHandler.getProxy();
		if(proxy != null) {
    		config.setProxy(proxy);
    		logger.debug("Configured API.AI proxy: {}", proxy);
    	}
    	
		return new AIDataService(config);
    }
    
    @Bean
    public OkHttpClient okHttpClient() {
    	Proxy proxy = proxyHandler.getProxy();
		OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxy);
    	logger.debug("Configured Facebook proxy: {}", proxy);
    	return builder.build();
    }
    
    
    @Bean
	public RestTemplate restTemplate(){
		RestTemplate rest = new RestTemplate();
		
		List<ClientHttpRequestInterceptor> ris = new ArrayList<>(1);
		ris.add(restLogger);
		
		rest.setInterceptors(ris);
		
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
	    requestFactory.setProxy(proxyHandler.getProxy());
		BufferingClientHttpRequestFactory requestFactory2 = new BufferingClientHttpRequestFactory(requestFactory);
		rest.setRequestFactory(requestFactory2);
		return rest;
	}
}

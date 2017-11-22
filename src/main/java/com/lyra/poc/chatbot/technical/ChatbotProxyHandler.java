package com.lyra.poc.chatbot.technical;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatbotProxyHandler{

	Proxy proxy;
	
	@Autowired
	public ChatbotProxyHandler() {
		String proxyHost = System.getProperty("http.proxyHost");
    	String proxyPortProperty = System.getProperty("http.proxyPort");
    	if(proxyHost != null && proxyPortProperty != null) {
    		Integer proxyPort = Integer.valueOf(proxyPortProperty);
    		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    	}
	}

	public Proxy getProxy() {
		return proxy;
	}
	
}

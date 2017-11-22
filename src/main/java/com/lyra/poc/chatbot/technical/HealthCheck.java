package com.lyra.poc.chatbot.technical;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthCheck implements HealthIndicator {
  
	@Autowired
	PayzenOrderGenerator orderGenerator;
	
	@Autowired
	private ChatbotProxyHandler proxyHandler;
	
	private static final Logger logger = LoggerFactory.getLogger("error");

	private static final String REASON_KEY = "Reason";

	@Override
    public Health health() {
    	
    	
    	String orderUrl = orderGenerator.generateOrder(115, "trash@lyra-network.com", "CB", "123456", "Dupont", "Martin");
    	
		try {
    		Proxy proxy = proxyHandler.getProxy();
    		URLConnection connection;
    		if(proxy != null) {
    			connection = new URL(orderUrl).openConnection(proxy);
    		}
    		else{
    			connection = new URL(orderUrl).openConnection();
    		}
			connection.addRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());

			if(connection instanceof HttpURLConnection) {
				int status = ((HttpURLConnection) connection).getResponseCode();
				if(status != 200) {
					return Health.down()
							.withDetail(REASON_KEY, "HTTP return code ("+status+") is different from 200")
							.build();
				}
			}
			
			InputStream response = connection.getInputStream();
			try (Scanner scanner = new Scanner(response)) {
			    String responseBody = scanner.useDelimiter("\\A").next();
			    if(!responseBody.contains("<span class=\"subtitle\"> Paiement sécurisé</span>") || 
			    		!responseBody.contains(">Valider</button>")) {
			    	return Health.down()
			    			.withDetail(REASON_KEY, "Payment page does not display correctly")
			    			.build();
			    }
			}
		} catch (IOException e) {
			logger.error("Found error while checking health", e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			return Health.down()
					.withDetail(REASON_KEY, "Could not connect to payment page: " + e.getMessage())
					.withDetail("Stacktrace", exceptionAsString)
					.build();
		} 
    	
        return Health.up().build();
    }
}
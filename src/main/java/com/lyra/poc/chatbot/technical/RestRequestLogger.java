package com.lyra.poc.chatbot.technical;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class RestRequestLogger implements ClientHttpRequestInterceptor{
	
	private static final Logger logger = LoggerFactory.getLogger("requests");

	  @Override
	   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
	         throws IOException {

	      traceRequest(request, body);
	      ClientHttpResponse clientHttpResponse = execution.execute(request, body);
	      traceResponse(clientHttpResponse);

	      return clientHttpResponse;
	   } 

	 private void traceRequest(HttpRequest request, byte[] body) throws IOException {
	      logger.info("RQ" + request.getMethod().name() + ' ' + request.getURI() + " : " + getRequestBody(body));
	   }

	   private String getRequestBody(byte[] body) throws UnsupportedEncodingException {
	      if (body != null && body.length > 0) {
	         return (new String(body, "UTF-8"));
	      } else {
	         return null;
	      }
	   }


	  private void traceResponse(ClientHttpResponse response) throws IOException {
	      logger.info("RP " + response.getStatusCode() + ' ' + response.getStatusText());
	   }

//	   private String getBodyString(ClientHttpResponse response) {
//	      try {
//	         if (response != null && response.getBody() != null) {// &&
//	                                                              // isReadableResponse(response))
//	                                                              // {
//	            StringBuilder inputStringBuilder = new StringBuilder();
//	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
//	            String line = bufferedReader.readLine();
//	            while (line != null) {
//	               inputStringBuilder.append(line);
//	               inputStringBuilder.append('\n');
//	               line = bufferedReader.readLine();
//	            }
//	            return inputStringBuilder.toString();
//	         } else {
//	            return null;
//	         }
//	      } catch (IOException e) {
//	         logger.error(e.getMessage(), e);
//	         return null;
//	      }
//	   }

}

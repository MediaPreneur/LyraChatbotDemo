package com.lyra.poc.chatbot;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.messenger4j.common.MessengerHttpClient;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class ProxyCapableMessengerHttpClient implements MessengerHttpClient {


	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";

	@Autowired
    private OkHttpClient okHttpClient;
    
    @Override
    public HttpResponse execute(HttpMethod httpMethod, String url, String jsonBody) throws IOException {
    	final Request.Builder requestBuilder = new Request.Builder().url(url);
    	if (httpMethod != HttpMethod.GET) {
    		final MediaType jsonMediaType = MediaType.parse(APPLICATION_JSON_CHARSET_UTF_8);
    		final RequestBody requestBody = RequestBody.create(jsonMediaType, jsonBody);
    		requestBuilder.method(httpMethod.name(), requestBody);
    	}
    	final Request request = requestBuilder.build();
    	try (Response response = this.okHttpClient.newCall(request).execute()) {
    		return new HttpResponse(response.code(), response.body().string());
    	}
    }
}

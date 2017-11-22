package com.lyra.poc.chatbot.technical;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Calls a Payzen webservice to create an order and create a link to its payment page
 * @author mregragui
 *
 */
@Component
public class PayzenOrderGenerator {

	private static final Logger logger = LoggerFactory.getLogger("webhook");
	
    private static final String PAYZEN_URL = "https://demo.payzen.eu/vads-payment/entry.silentInit.a";
    private static final String MODE = "TEST";
    private static final String MERCHANT_TEST_KEY = "A-COMPLETER";
    private static final String MERCHANT_SITE_ID = "A-COMPLETER";

    private String redirectUrl;
    private RestTemplate restTemplate;

    @Autowired
    public PayzenOrderGenerator(@Value("${public.url}") String publicUrl, RestTemplate restTemplate) {
        this.redirectUrl = publicUrl + "redirection";
        this.restTemplate = restTemplate;
    }

    /**
     * Creates a new order on Payzen
     * @param amount
     * @param email null if none provided
     * @param cardType : allowed values are CB, VISA or MASTERCARD
     * @param senderId facebook identifier of the user
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return the link to the payment page for the order
     */
    public String generateOrder(int amount, String email, String cardType, String senderId, String firstName, String lastName) {
        logger.info("Génération d'une commande de " + amount + " type carte " + cardType);
        MultiValueMap<String, String> parameters = createParameters(amount, email, cardType, senderId, firstName, lastName);

        String signature = computeSignature(parameters);
        parameters.add("signature", signature);

        return post(parameters);
    }

    private String post(MultiValueMap<String, String> parameters) {
    	
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

        try {
        	logger.debug("PayzenOrderGenerator: posting");
            ResponseEntity<JsonNode> result = restTemplate.postForEntity(PAYZEN_URL, request, JsonNode.class);
            String url = result.getBody().get("redirect_url").asText();
            //XXX HACK
            url = url.replace(":443", "");
            //XXX END HACK
            logger.debug("PayzenOrderGenerator: url result {}", url);
            return url;
        } catch(RestClientException e) {
        	logger.error("PayzenOrderGenerator error while posting: ", e);
            String msg = e.getMessage();
            throw new RuntimeException("Erreur " + msg, e);
        } 
    }

    private MultiValueMap<String, String> createParameters(int amount, String email, String cardType, String senderId, String firstName, String lastName){
        MultiValueMap<String, String> mapParams = new LinkedMultiValueMap<>();

        mapParams.add("vads_amount", String.valueOf(amount));
        mapParams.add("vads_version", "V2");
        mapParams.add("vads_ctx_mode", MODE);
        mapParams.add("vads_currency", "978");
        mapParams.add("vads_page_action", "PAYMENT");
        mapParams.add("vads_payment_config", "SINGLE");
        mapParams.add("vads_site_id", MERCHANT_SITE_ID);

        Date dNow = new Date();
        SimpleDateFormat transactionDate = new SimpleDateFormat ("yyyyMMddHHmmss");
        SimpleDateFormat transactionId = new SimpleDateFormat ("hhmmss");
        mapParams.add("vads_trans_date",transactionDate.format(dNow));
        mapParams.add("vads_trans_id",transactionId.format(dNow));

        mapParams.add("vads_action_mode", "INTERACTIVE");

        mapParams.add("vads_return_mode","GET");


        mapParams.add("vads_url_return", buildRedirectUrl(senderId, "return"));
        mapParams.add("vads_url_success", buildRedirectUrl(senderId, "success"));
        mapParams.add("vads_url_refused", buildRedirectUrl(senderId, "refused"));
        mapParams.add("vads_url_cancel", buildRedirectUrl(senderId, "cancel"));
        mapParams.add("vads_url_error", buildRedirectUrl(senderId, "error"));

        if(email!=null) {
            mapParams.add("vads_cust_email",email);
        }
        mapParams.add("vads_cust_first_name", firstName);
        mapParams.add("vads_cust_last_name", lastName);

        mapParams.add("vads_payment_cards", cardType);

        mapParams.add("vads_language","fr");

        return mapParams;
    }
    
    private String buildRedirectUrl(String senderId, String status) {
        return redirectUrl + "?facebookId=" + senderId + "&paymentStatus=" + status;
    }

    private String computeSignature(MultiValueMap<String, String> parameters) {
        StringBuilder concatenateMapParams = new StringBuilder();

        parameters.keySet().stream().sorted(COMPARATOR).map(key -> parameters.get(key)).forEach(values -> {
            String v = values.get(0);
            concatenateMapParams.append(v).append("+");
        });

        concatenateMapParams.append(MERCHANT_TEST_KEY);

        try {
            return sha1(concatenateMapParams.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot compute signature", e);
        }
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private static final Comparator<? super String> COMPARATOR = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };
    
}

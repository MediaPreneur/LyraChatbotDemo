This repository is intended to demonstrate how a merchant can use Payzen payments in a chatbot.

The example code in this repository is a web application, that acts as a chatbot server, built on Spring Boot. 
It uses Dialog Flow as an NLP provider


Quick start

Create a Facebook app and a DialogFlow project.

Clone this repository

In the /chatbot/src/main/resources/config/application.properties file, set the properties for DialogFlow (api.ai) and Messenger4j

In the com.lyra.poc.chatbot.technical.PayzenOrderGenerator class, set the MERCHANT_TEST_KEY and MERCHANT_SITE_ID constants

Run the com.lyra.poc.chatbot.App class
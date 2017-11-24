# What is this repository for?
---

This repository is intended to demonstrate how a merchant can use Payzen payments in a chatbot.

The example code in this repository is a web application, that acts as a chatbot server, built on Spring Boot. 
It uses Dialog Flow as an NLP provider.


## Quick start

* Clone this repository.
* Create a Facebook Page, as well as a Facebook app with the Messenger product. See [the Facebook developer documentation](https://developers.facebook.com/docs/apps/register). Write down your app's app secret and page access token; write down a verification token - it can be any string you like.
* Create a DialogFlow project, and add the intents needed to implement the scenarios of your shopping flow. See [the Dialogflow documentation](https://dialogflow.com/docs/getting-started/basics).  Write down your dialogflow api key.
* A handful of key intents need to be created in DialogFlow to handle payment:
  * __payment__: Handles sentences where the client states his intention to pay, and how much he should pay. Requires parameter __amount__; optionally, handles parameter __centAmount__ to handle phrases such as "I would like to pay 5 euros and 12 cents". Should ask the client to input his email address as a response.
    * If you wish to change this interaction (and in particular make it so the client doesn't have to input the amount of his payment), see com.lyra.poc.chatbot.action.ApiAICheckoutHandler. Your shopping cart logic will have to set the payment amount in the client's ConversationContext.
  * __email__: Follow-up intent to __payment__. Handles sentences where the client inputs an email adress. Requires parameter __mail__.
  * __email.none__: Another follow-up intent to __payment__. Handles sentences where the client declines to input an email address.
* In the /chatbot/src/main/resources/config/application.properties file, set the properties for DialogFlow (api.ai) and Messenger4j using the keys and secrets you wrote down earlier, as well as your server's public URL.
* In the com.lyra.poc.chatbot.technical.PayzenOrderGenerator class, set the MERCHANT\_TEST\_KEY and MERCHANT\_SITE\_ID constants to match your shop's test key and ID, respectively.
* Run the com.lyra.poc.chatbot.App class.
  * If you are running the App from behind an outgoing proxy, make sure you use vm arguments -DhttpProxyHost and -Dhttp.proxyPort
* Add the webhook product to your Facebook app. When prompted to, input the URL https://your-public-url/webhook, your verification token, and select subscriptions messages, messaging_optins and messaging_postbacks. See [the Facebook webhook documentation](https://developers.facebook.com/docs/apps/register) for details.
* When testing your Facebook app, remember that until it is published it will only respond to Facebook users with Roles Tester or above. See [the Facebook development cycle documentation](https://developers.facebook.com/docs/apps/register) for details.

----
## changelog
* 23-Nov-2017 V1
* 24-Nov-2017 Migrated to markdown, added some additional info.
# BotForge

***DISCLAIMER: This software is still in beta. If you want to use it in production, do it at your own risk.***

## Overview

**BotForge** is a light framework that allows you to easily create Facebook Messenger Bots. It uses basic Java HTTP Servlets under the hood and has no dependencies other than Gson, so that you can deploy it on your favorite Servlet Container, either in the cloud or on-premises.

BotForge takes care of handling the low level interaction with the Facebook API and saves you writing a lot of boilerplate code. It automatically handles basic functionalities (e.g. webhook validation and signature verification), exposes a simple event based API for message reception and provides facilities to manage the received messages, the sending of new messages and the querying of the User Profile API. It also has out-of-the-box the ability to impersonate multiple bots with the same code (more on this later).

Creating a HelloBot that greets users by calling them with their name is as simple as writing a few lines of code:

```java
public class HelloBot extends FbBot {
    
    @Override
    protected BotContext onContextLoad(String pageId, String webhookUrl) {
        return new BotContext(
                PAGE_ID,
                PAGE_ACCESS_TOKEN,
                APP_SECRET_KEY,
                VERIFY_TOKEN,
                WEBHOOK_URL
        );
    }
    
    @Override
    protected void onMessageReceived(BotContext context, ReceivedMessage message) {
        String userId = message.getSenderId();
        String pageAccessToken = context.getPageAccessToken();
        
        SendMessageAdapter.sendTextMessage(
                pageAccessToken,
                "Hello " + UserProfileApiAdapter.getUserProfile(pageAccessToken, userId).getFirstName() + "!",
                userId
        );
    }
    
}
```

Let's analyze the code above: 

* On line 1, the `FbBot` class is extended. This class is the abstract class the every bot shall extend.

* On line 4, the callback for loading bot context objects is overridden. Whenever BotForge needs a bot context and can't find it in the cache, it will invoke the `onContextLoad()` callback so that the right bot context can be created (if needed) and returned.

* On line 5, a new `BotContext` object is created and returned, using all the relevant information about the bot.

* On line 15, the callback for message receiving is overridden. Whenever a message is received from a user, BotForge will invoke the `onMessageReceived()` callback passing as arguments the message sent from the user and the bot context object of the bot that received the message.

* On line 16, the Sender ID is extracted from the received message. This will become the Recipient ID for the new message.

* On line 17, the Page Access Token is extracted from the bot context passed as an argument. This will be used to send a message to the user.
 
* On line 19, a new basic text message is sent to the user using the Page Access Token, the String representing the text message and the User ID.

* On line 21, the User Profile API is queried to retrieve the first name of the user.


## BotForge Internals

In BotForge the `FbBot` class is the superclass that shall be extended by every bot's main class (the one that will handle the incoming messages). The `FbBot` class, in turn, extends the `HttpServlet` class. So you have to configure the deployment descriptor of your app (most likely the web.xml file) to use the bot's main class as the servlet for the URL you have chosen as your bot's webhook.

### Webhook handling

The `doPost()` method of the `FbBot` class will receive all the messages that the Facebook servers will send to the webhook of your bot. These messages are in the form of HTTP POST requests, carrying data in JSON format.
 
A request can carry user's messages or other message-related events, such as postbacks, delivery receipts, read receipts, etc. Moreover, each request can contain a batch of messages. Facebook can do this to optimize resource usage (e.g. in case of heavy load). 

BotForge can handle all these events seamlessly.
  
Whenever a message is received from the Facebook servers, BotForge will first try to load the context of the bot that is receiving the message (more on this later) based on the URL used by the Facebook servers. After that, BotForge will go through all the messages of the batch and for each one of them will determine its type, create the right POJO with the content extracted from the message and, finally, will invoke the right callback passing the bot context and the newly created POJO.
  
In BotForge there are a number of callbacks that you can override to handle all the possible events that can be generated during the bot activity. 

For example, to handle the reception of user's messages you can override the `onMessageReceived()` callback. This way, every time a user sends a message to your bot, your implementation of `onMessageReceived` will be called by BotForge and you will have access to the `BotContext` object and to the `ReceivedMessage` object. Similarly, you can override `onPostbackReceived` callback to handle the postbacks from your users, and so on.
 
It's important to notice that all this callbacks have a default implementation that does just nothing, so you are free to override only the callbacks related to the events that you are really interested to handle, leaving out the rest.

You can find the complete list of callbacks by looking at the JavaDocs of the `FbBot` class.

### Incoming Messages

As stated before, each incoming messaging event has a POJO that models it and can hold its content. All of them have `IncomingMessage` as the common superclass. This means that for every messaging event you can access its Sender ID, Recipient ID and Timestamp via getter methods. 

Of course, you can have access to the data of a specific message type using the appropriate getter methods for that message. For example, you can use `getPayload()` to get the payload of a `Postback` message, `getAttachment()` to get the attachment of an `IncomingAttachmentMessage`, or `getText()` to get the text of an `IncomingTextMessage`.
 
You can find the complete list of POJOs for incoming messaging events and their getter methods by looking at the JavaDocs of BotForge.

### Outgoing Messages

One of the main feature of a chat bot is, obviously, to send chat messages. The Facebook Messenger Platform supports a certain number of message types which can be further customized adding things like quick replies, notification options, etc. You can find a complete description of all the supported message types and the allowed options by looking at the [official documentation of the Send API](https://developers.facebook.com/docs/messenger-platform/send-api-reference).

BotForge has a simple and unified interface to create every kind of outgoing message with every kind of possible customization. You can use a builder to create the desired type of message and then use that builder to customize it. When you are done, you will get an instance of `OutgoingMessage` object that is ready to be sent. 

In order to create a new `OutgoingMessage` you have to create a new instance of `OutgoingMessage.Builder` object, passing the message type to the constructor. You can then use the builder's public methods to customize your message. When you are done, just invoke the `build()` method to get your message. Please note that if you try to customize you message in ways that are not supported by the message type, an exception will be thrown.

Here is an example for creating a template message with quick replies.

```java
Bubble bubble = new Bubble().setTitle("Example Bubble").setImageUrl("http://example.com/bubble.jpg");

OutgoingMessage.QuickReply quickReply1 = new OutgoingMessage.QuickReply("Action 1", "act1");
OutgoingMessage.QuickReply quickReply2 = new OutgoingMessage.QuickReply("Action 2", "act2");
   
OutgoingMessage.Builder builder = new OutgoingMessage.Builder(OutgoingMessageType.TEMPLATE_GENERIC);
    
OutgoingMessage message = builder.addBubble(bubble)
    .addQuickReply(quickReply1)
    .addQuickReply(quickReply2)
    .build();
```

You can find the complete list of option for the builder by looking at the JavaDocs of the `OutgoingMessage.Builder` class.  

### Send Message Adapter

Once you have the OutgoingMessage, you can send it using the `sendMessage()` static method of the `SendMessageAdapter` class.

In order to use that method, you will need two things other than the message you have just created: the Page Access Token for your bot and an instance of `OutgoingMessage.OutgoingRecipient` object.

The Page Access Token can be extracted from the `BotContext` object of your bot. As a reminder, this object is passed as an argument to every webhook-related callback.
 
The recipient object can be created using the appropriate constructor. Please note that you can use a User ID or a telephone number in the constructor, but not both. If you are willing to use telephone numbers as recipient, make sure you are allowed by Facebook to do so, otherwise you will get an error in response to the message sending. 

The `sendMessage()` method will perform a synchronous HTTP request to the Facebook servers, will deliver the message and will return the response of the Facebook servers in the form of a `SendMessageResponse` object. You can inspect this object to check for errors or to get the response-related information.

You can send the same `OutgoingMessage` to multiple recipients by using the overloaded version of the `sendMessage()` method that will accept an array of `OutgoingMessage.OutgoingRecipient` objects as the third parameter. You will get an array of `SendMessageResponse` objects that will match the order of the recipients in the array. 

Please note that this method will perform a synchronous HTTP request to the Facebook servers for each recipient. So if you have constraints about the execution time of your code in your environment it's better to call this method in a different thread (or in a task queue) or, at least, limit the number of recipient to a minimum.

If you just want to send basic messages, the `SendMessageAdapter` offers a collection of methods that will let you do so without having to create the message with a builder. There is one of these methods for each message type. For example, to send a basic text message all you need is this code:
```
SendMessageAdapter.sendTextMessage(
    PAGE_ACCESS_TOKEN,
    "Hi, this is a sample message",
    "RECIPIENT_ID"
);
```
Every one of these methods has an overloaded version that will allow you to send the message to multiple recipients. Please note that this way you can only send messages using User IDs.

### Message Converter

BotForge offers a facility to convert a message received from a user into an `OutgoingMessage`. This can be useful in case you want to forward messages you receive.

To do so, just call the `getOutgoingMessage()` static method of the `MessageConverter` class, passing as an argument the instance of the message you received via the `onMessageReceived()` callback.

### User Profile API Adapter

BotForge also offers an adapter to query the Facebook's User Profile API. 

If you want to retrieve information about a user, all you need to do is invoking the `getUserProfile()` static method of the `UserProfileApiAdapter` class, passing the User ID as an argument. This method will perform a synchronous HTTP request to the Facebook servers in order to retrieve the desired user profile. This profile will be returned in the form of a `UserProfile` object. You can access user's information via the getter methods of that object.

### Bot Contexts and BotContextManager

Bot contexts are a core component of BotForge. A context defines a bot and contains all the information about a specific bot that is needed for that bot to work (e.g. Webhook URL, Page Access Token, App Secret Key, etc.), together with some configuration parameters (e.g. whether or not debug and signature verification are enabled, etc.).

Contexts are used in BotForge to abstract the concept of a bot and to simplify the management of bot-related configuration, allowing the rest of the framework to operate atomically for every messaging event by extracting information from a common data source. 

Incidentally this method, together with the RESTful nature of the Messenger Platform, also offers the opportunity to treat the bots as "virtual bots", allowing the same code to act like different bots at the same time. This happens because, for every request received by the Facebook servers, the right bot context is picked and passed to the appropriate callback. The webhook URL, used by the Facebook servers to perform the request, acts like an identifier for the contexts allowing the framework to pick the right one every time.
 
Moreover, since the contexts are handled using the `BotContextManager`, you can dynamically add, remove or update contexts at runtime. You can do this by using the instance of the manager that is available as a field in the `FbBot` class and invoking the appropriate methods. 

#### Managing the contexts

All the bot contexts must be manually created by the developer programmatically. This is easily done by using the constructor of the `BotContext` class.

In the most basic scenario in which you only need to create one bot (probably the most common scenario), you will only need one `BotContext` object.
 
No matter if you have one context or multiple contexts, you will need to load them in the framework at a certain point. BotForge gives you two ways to do this: you can either bulk load contexts at servlet initialization time, or lazy load them individually when they are needed.

When the servlet is first instantiated the `botInit()` method is invoked by BotForge and it is expected to return an instance of `List<BotContext>`. You are not forced to override this method, but you can do it if you want to perform some initialization and bulk load the bot contexts. Any `BotContext` that you will return in this method will be loaded inside the `BotContextManager`. If you just want to perform initialization but you don't want any contexts loaded at this time, it's fine to return null.

If you want to lazy load the contexts only when they are needed, you can use the `onContextLoad()` method. BotForge will invoke this method only when the needed context is not found inside the cache of the `BotContextManager`. Please note that this method is declared abstract, so you are forced to implement it. However, if you plan to load the contexts elsewhere, just return null in your implementation.

The `onContextLoad()` method has two string arguments, `pageId` and `webhookUrl`, and it is supposed to return a bot context. The framework can pass one argument or the other, but not both at the same time. Your implementation is supposed to work no matter what parameter is passed. You have to use the provided parameter as an identifier to retrieve or create the bot context before returning it. Of course, if you only need one bot, you can just create or retrieve its context without caring about the parameters. Please note that this method will be invoked only once for every bot context during the servlet lifetime, since the `BotContextManager` will provide a cached value the next time the same context is needed. So, if you need to update a context at runtime, please do so by accessing the `BotContextManager` directly.
  
For further information on `BotContext` or `BotContextManager` objects, please refer to their JavaDocs.

### Webhook Validation

Facebook requires the bot to correctly handle challenge requests performed to validate the webhook of your bot. These requests are just HTTP GET requests performed by the Facebook servers to the webhook URL you have chosen for your bot.
 
BotForge will take care of handling these requests for you automatically, without you writing a single line of code. You just need to insert the right Verify Token in the bot context when you instantiate it.

### Signature

Unless you specify otherwise in the `BotContext` object of your bot, BotForge will check the signature of every message received from the Facebook servers. This is done by using the App Secret Key contained in the context.

If the verification fails, an error will be returned as the response of the HTTP request and the message will not be processed.

## Using BotForge

Before you start coding your bot, make sure you are familiar with the preliminary setup needed for your bot to work by looking at the Facebook Messenger Platform's [Getting Started guide](https://developers.facebook.com/docs/messenger-platform/quickstart). You will need some of this information to create the context of your bot. You will also need to decide which messaging event callbacks you are going to override based on the subscription fields you are willing to check in your webhook preferences. 

### Including the library in your project

The first step to work with BotForge is to include the library in your project. To do so, download the latest version of the jar from [the release section on GitHub](https://github.com/NinetySlide/BotForge/releases), copy it into the library folder of your project and add it as a library. This process will be different based on the IDE you are currently using.

Remember that BotForge requires Gson as a dependency. In the release section you can find two jars: one contains just BotForge, while the other also has Gson included in the package. If you already have Gson as a dependency of your project or you want to keep Gson separated from BotForge, pick the former. Otherwise you can safely pick the latter.

>***Support for including BotForge via Maven and Gradle is coming soon!***

### Servlet Configuration

Once you decide what the URL of your webhook will be and which class in your project is going to process the data received by your webhook from the Facebook servers, you need to configure the deployment descriptor (most likely the `web.xml` file) to let your class receive the data.

For example, if the class is named `HelloBot` and the relative URL of your webhook is `/webhook`, your deployment descriptor will look something like this:

```xml
<web-app>
    <servlet>
        <servlet-name>HelloBot</servlet-name>
        <servlet-class>com.example.bot.HelloBot</servlet-class>
    </servlet>
 
    <servlet-mapping>
        <servlet-name>HelloBot</servlet-name>
        <url-pattern>/webhook</url-pattern>
    </servlet-mapping>
</web-app>
```

### Initial Code Setup

At this point, you need to populate the `HelloBot` class. The first thing you need to do is to implement the `onContextLoad()` method. In your implementation you are going to create and return a `BotContext` instance with the configuration information related to your bot.
 
If you want to perform one time initializations or bulk load some bot contexts, you will need to override the `botInit()` method. This method is expected to return a list of `BotContext` objects, but if you don't have any contexts to load here, it is ok to return null.

Please refer to the section of this readme covering bot contexts for further details.

### Receiving Messages

For the bot to be of any use, you will probably want to override one of the messaging event callbacks. You can start with the `onMessageReceived()` callback, so you will be able to receive messages from users.

Based on the subscription fields that you checked in your webhook preferences, you will probably want to override other callbacks. For example, if you have chosen to receive postbacks and message read receipts, you have to override the `onPostbackReceived()` and `onMessageRead()` callbacks.

So, at this point, your code will look something like this:

```java
@Override
protected void onMessageReceived(BotContext context, ReceivedMessage message) {
    /*****************************/
    /* Your implementation here! */
    /*****************************/
}

@Override
protected void onPostbackReceived(BotContext context, Postback message) {
    /*****************************/
    /* Your implementation here! */
    /*****************************/
}

@Override
protected void onMessageRead(BotContext context, ReadReceipt message) {
    /*****************************/
    /* Your implementation here! */
    /*****************************/
}
```

As you can notice, the first argument of each callback is always the bot context of the bot that received the message. The second argument, instead, is a POJO representing the message received by the bot. Each callback has a specific POJO mapping the received message. You can access all the parameters of every message by using getter methods provided by BotForge for that specific POJO.

Please look at the Facebook's [Webhook Reference](https://developers.facebook.com/docs/messenger-platform/webhook-reference) and at the JavaDocs of BotForge for further details.

### Sending Messages

BotForge offers two easy methods to create and send messages to the users. You can either use helper methods to quickly create and send basic messages or instantiate a builder and use it to create a message with each possible option.

If you choose the latter option, you will end up with an instance of `OutgoingMessage` object. At this point you have to use the `sendMessage()` static method of the `SendMessageAdapter` class to actually send the message to the user.
 
Please refer to the sections of this readme covering Outgoing Messages and Send Message Adapter for further details.

### More Complex Code Samples

A showcase implementation named MultiBot made using the BotForge is available as OpenSource under the Apache 2.0 license. You can find it [here](https://github.com/NinetySlide/MultiBot).

Other implementations/code samples are coming soon.

## Known Issues and Missing Features

BotForge supports v1.1 of Messenger Platform with some exceptions. Those exceptions are:

* Airline related templates.

* Receipt template.

* File uploading for incoming and outgoing Attachment Messages.

* Template and fallback messages in Echo Messages.

* Thread Settings management via an adapter.

## Authors, Contributions and Contacts

As the licence reads, this is free software released by NinetySlide under the Apache License Version 2.0. The author (Marcello Morena) will continuously work to improve this framework, but external contributions are more than welcome. If you want to contribute, start by looking at the TODO file included in the project to know what the priorities are. You can then fork the project on GitHub and create a pull request with your modifications. For everything else you can just mail us at opensource[at]ninetyslide[dot]com.

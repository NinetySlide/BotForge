# BotForge

***DISCLAIMER: This software is still in beta. If you want to use it in production, do it at your own risk.***

## Overview

**BotForge** is a light framework that allows you to easily create Facebook Messenger Bots. It uses basic Java HTTP Servlets under the hood and has no dependencies other than Gson, so that you can deploy it on your favorite Servlet Container, either in the cloud or on-premises.

BotForge takes care of handling the interaction with the Facebook API and saves you writing a lot of boilerplate code. It exposes a simple event based API for message reception, automatically handles basic functionalities (e.g. webhook validation and signature verification) and provides facilities to manage the received messages, the sending of new messages and the querying of the User Profile API. It also has out-of-the-box the ability to impersonate multiple bots with the same code (more on this later).

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
 - On line 1, the `FbBot` class is extended. This class is the abstract class the every bot shall extend.
 - On line 4, the callback for loading bot context objects is overridden. Whenever BotForge needs a bot context and can't find it in the cache, it will invoke the `onContextLoad()` callback so that the right bot context can be created (if needed) and returned.
 - On line 5, a new `BotContext` object is created and returned, using all the relevant information about the bot.
 - On line 15, the callback for message receiving is overridden. Whenever a message is received from a user, BotForge will invoke the `onMessageReceived()` callback passing as arguments the message sent from the user and the bot context object of the bot that received the message.
 - On line 16, the Sender ID is extracted from the received message. This will become the Recipient ID for the new message.
 - On line 17, the Page Access Token is extracted from the bot context passed as an argument. This will be used to send a message to the user. 
 - On line 19, a new basic text message is sent to the user using the Page Access Token, the String representing the text message and the User ID.
 - On line 21, the User Profile API is queried to retrieve the first name of the user.

## BotForge Internals
### Architecture and Bot Lifecycle
In BotForge the bot's main class (the one that will handle the incoming messages) shall extend the `FbBot` class. This class, in turn, extends the `HttpServlet` class, so in order to receive messages you have to configure the deployment descriptor (most likely the web.xml file) to use the bot's main class as the servlet for the URL you choose as your bot's webhook.

When the servlet is first instantiated ... 

Whenever a message is received from the Facebook servers ...

To send a new message ...

### Bot Contexts
ph

### Webhook Validation
ph

### Signature
ph

## Using BotForge
### Including the library in your project (asap maven/gradle and javadocs) 
ph

### Servlet Configuration
ph
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
ph

### Receiving Messages
ph

### Sending Messages
ph

### Other Features
#### User Profile API
ph

#### Thread Settings
ph

### More Complex Code Samples
>***Coming Soon!***

## Known Issues and Missing Features
BotForge supports v1.1 of Messenger Platform with some exceptions. Those exceptions are:
* Ph

## Authors, Contacts and Contributions
ph

# BotForge

***DISCLAIMER: This software is still in beta. If you want to use it in production, do it at your own risk.***

## Overview (with basic code samples?)

**BotForge** is a framework that allows you to easily create Facebook Messenger Bots. It uses basic Java HttpServlets under the hood and has no dependencies other than Gson, so that you can deploy it on your favorite Servlet Container, either in the cloud or on-premises.

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

## BotForge Internals
### Architecture and Bot Lifecycle
ph

### Bot Contexts
ph

### Webhook Validation
ph

### Signature
ph

## Using BotForge
### Including the library in your project and servlet configuration (asap maven/gradle and javadocs)
ph

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
ph

## Authors, Contacts and Contributions
ph

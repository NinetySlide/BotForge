/*
 * Copyright 2016 NinetySlide
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninetyslide.libs.botforge.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetyslide.libs.botforge.FbBot;
import com.ninetyslide.libs.botforge.common.Constants;
import com.ninetyslide.libs.botforge.core.BotContext;
import com.ninetyslide.libs.botforge.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageError;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageResponse;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageSuccess;
import com.ninetyslide.libs.botforge.util.GsonManager;
import com.ninetyslide.libs.botforge.util.NetworkManager;

import java.util.logging.Logger;

/**
 * Class that provides the facilities to access the Send API.
 */
public final class SendMessageAdapter {

    private static final Logger log = Logger.getLogger(FbBot.class.getName());

    private final static String SEND_MESSAGE_BASE_URL = "https://graph.facebook.com/v2.6/me/messages?access_token=";

    private static Gson gson = GsonManager.getGsonInstance();
    private static JsonParser jsonParser = GsonManager.getJsonParserInstance();

    private SendMessageAdapter() {
    }

    /**
     * Send a message from a specific bot.
     *
     * @param context The Context of the bot to use for message sending.
     * @param message The message to send.
     * @param recipient The recipient for the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendMessage(BotContext context, OutgoingMessage message, OutgoingMessage.OutgoingRecipient recipient) {
        // Check that all the parameters are ok
        if (context == null) {
            throw new IllegalArgumentException(Constants.MSG_CONTEXT_INVALID);
        }
        if (message == null) {
            throw new IllegalArgumentException(Constants.MSG_MESSAGE_INVALID);
        }
        if (recipient == null) {
            throw new IllegalArgumentException(Constants.MSG_RECIPIENT_INVALID);
        }

        // Set the recipient for the message
        message.setRecipient(recipient);

        // Generate the JSON String
        String jsonStrToSend = gson.toJson(message).replace("'", "\\'");

        // Log the request data if debug is enabled
        if (context.isDebugEnabled()) {
            log.info("JSON Raw Message: " + jsonStrToSend);
        }

        // Perform the request
        String response = NetworkManager.performPostRequest(
                SEND_MESSAGE_BASE_URL + context.getPageAccessToken(),
                jsonStrToSend
        );

        // Parse the response
        if (response != null) {
            // Parse the String into a JsonObject
            JsonObject jsonResponse = jsonParser.parse(response).getAsJsonObject();

            // Check for errors
            JsonObject error = jsonResponse.getAsJsonObject(Constants.JSON_SEND_RESPONSE_FIELD_NAME_ERROR);

            if (error != null) {
                // Return an error if the response contains an error
                return gson.fromJson(error, SendMessageError.class);
            } else {
                // Return a success response otherwise
                return gson.fromJson(jsonResponse, SendMessageSuccess.class);
            }

        } else {
            // Return a generated network error if something wrong happened during the network request
            return SendMessageError.generateNetworkError();
        }
    }

    /**
     * Send a message from a specific bot in bulk to a number of recipients.
     *
     * @param context The Context of the bot to use for message sending.
     * @param message The message to send.
     * @param recipients The recipients for the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendMessage(BotContext context, OutgoingMessage message, OutgoingMessage.OutgoingRecipient[] recipients) {
        // Check that all the parameters are ok
        if (recipients == null) {
            throw new IllegalArgumentException(Constants.MSG_RECIPIENT_INVALID);
        }

        // Create the array of responses
        SendMessageResponse[] responses = new SendMessageResponse[recipients.length];

        // Perform the requests, one for each recipient
        for (int i = 0; i < recipients.length; i++) {
            responses[i] = sendMessage(context, message, recipients[i]);
        }

        // Return the responses array
        return responses;
    }

    /**
     * Send a basic text message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param text The content of the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendTextMessage(BotContext context, String text, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                .setText(text)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic text message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param text The content of the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendTextMessage(BotContext context, String text, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                .setText(text)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Send a basic audio message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param audioUrl The URL of the resource attached to the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendAudioMessage(BotContext context, String audioUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.AUDIO)
                .setMediaUrl(audioUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic audio message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param audioUrl The URL of the resource attached to the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendAudioMessage(BotContext context, String audioUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.AUDIO)
                .setMediaUrl(audioUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Send a basic image message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param imageUrl The URL of the resource attached to the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendImageMessage(BotContext context, String imageUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.IMAGE)
                .setMediaUrl(imageUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic image message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param imageUrl The URL of the resource attached to the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendImageMessage(BotContext context, String imageUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.IMAGE)
                .setMediaUrl(imageUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Send a basic video message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param videoUrl The URL of the resource attached to the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendVideoMessage(BotContext context, String videoUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.VIDEO)
                .setMediaUrl(videoUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic video message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param videoUrl The URL of the resource attached to the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendVideoMessage(BotContext context, String videoUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.VIDEO)
                .setMediaUrl(videoUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Send a basic file message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param fileUrl The URL of the resource attached to the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendFileMessage(BotContext context, String fileUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.FILE)
                .setMediaUrl(fileUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic file message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param fileUrl The URL of the resource attached to the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendFileMessage(BotContext context, String fileUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.FILE)
                .setMediaUrl(fileUrl)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Send a basic action message to a specific recipient using its ID (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient and pass them as arguments to the sendMessage() method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param action The action to send with the message.
     * @param recipientId The ID of the recipient of the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong. Use the 
     * hasError() method on the returned object to determine the type of object to cast.
     */
    public static SendMessageResponse sendAction(BotContext context, OutgoingMessage.SenderAction action, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.SENDER_ACTION)
                .setSenderAction(action)
                .build();

        // Send the message
        return sendMessage(context, message, getOutGoingRecipient(recipientId));
    }

    /**
     * Send a basic action message to multiple recipients using their IDs (not phone number). If you need more options, 
     * manually create an OutgoingMessage, an OutgoingRecipient array and pass them as arguments to the sendMessage() 
     * method.
     *
     * @param context The Context of the bot to use for message sending.
     * @param action The action to send with the message.
     * @param recipientIds The IDs of the recipients of the message.
     * @return The array of responses, one for each recipient. Each response can be a SendMessageSuccess instance or a 
     * SendMessageError instance, if something went wrong. Use the hasError() method on the returned object to 
     * determine the type of object to cast. Please note that the response in the n-th position is related to the 
     * recipient in the n-th position.
     */
    public static SendMessageResponse[] sendAction(BotContext context, OutgoingMessage.SenderAction action, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.SENDER_ACTION)
                .setSenderAction(action)
                .build();

        // Send the message
        return sendMessage(context, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Create an OutgoingRecipient object starting with a Recipient ID.
     *
     * @param recipientId The ID of the recipient of the message.
     * @return The resulting OutgoingRecipient.
     */
    private static OutgoingMessage.OutgoingRecipient getOutGoingRecipient(String recipientId) {
        return new OutgoingMessage.OutgoingRecipient(null, recipientId);
    }

    /**
     * Create an array of OutgoingRecipient objects starting with an array of Recipient IDs.
     *
     * @param recipients  The IDs of the recipients of the message.
     * @return The resulting OutgoingRecipient array.
     */
    private static OutgoingMessage.OutgoingRecipient[] getOutgoingRecipients(String[] recipients) {
        OutgoingMessage.OutgoingRecipient[] outgoingRecipients = new OutgoingMessage.OutgoingRecipient[recipients.length];

        for (int i = 0; i < recipients.length; i++) {
            outgoingRecipients[i] = new OutgoingMessage.OutgoingRecipient(null, recipients[i]);
        }

        return outgoingRecipients;
    }

}

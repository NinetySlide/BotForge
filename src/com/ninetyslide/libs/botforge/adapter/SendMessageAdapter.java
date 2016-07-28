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
import com.ninetyslide.libs.botforge.common.Constants;
import com.ninetyslide.libs.botforge.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageError;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageResponse;
import com.ninetyslide.libs.botforge.core.message.outgoing.response.SendMessageSuccess;
import com.ninetyslide.libs.botforge.util.GsonManager;
import com.ninetyslide.libs.botforge.util.NetworkManager;

/**
 * Class that provides the facilities to access the Send API.
 */
public final class SendMessageAdapter {

    private final static String SEND_MESSAGE_BASE_URL = "https://graph.facebook.com/v2.6/me/messages?access_token=";

    private static Gson gson = GsonManager.getGsonInstance();
    private static JsonParser jsonParser = GsonManager.getJsonParserInstance();

    private SendMessageAdapter() {
    }

    /**
     * Method used to send a message from a certain bot.
     *
     * @param pageAccessToken The Page Access Token to use for message sending.
     * @param message The message to send.
     * @param recipient The recipient for the message.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong.
     */
    public static SendMessageResponse sendMessage(String pageAccessToken, OutgoingMessage message, OutgoingMessage.OutgoingRecipient recipient) {
        // Check that all the parameters are ok
        if (pageAccessToken == null) {
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

        // Perform the request
        String response = NetworkManager.performPostRequest(
                SEND_MESSAGE_BASE_URL + pageAccessToken,
                gson.toJson(message)
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
     * Message used to send a message from a certain bot in bulk to a number of recipients.
     *
     * @param pageAccessToken The Page Access Token to use for message sending.
     * @param message The message to send.
     * @param recipients The recipients for the message.
     * @return The array of responses, one for each recipient. Please note that the response in the n-th position is
     * related to the recipient in the n-th position.
     */
    public static SendMessageResponse[] sendMessage(String pageAccessToken, OutgoingMessage message, OutgoingMessage.OutgoingRecipient[] recipients) {
        // Check that all the parameters are ok
        if (recipients == null) {
            throw new IllegalArgumentException(Constants.MSG_RECIPIENT_INVALID);
        }

        // Create the array of responses
        SendMessageResponse[] responses = new SendMessageResponse[recipients.length];

        // Perform the requests, one for each recipient
        for (int i = 0; i < recipients.length; i++) {
            responses[i] = sendMessage(pageAccessToken, message, recipients[i]);
        }

        // Return the responses array
        return responses;
    }

    /* TODO: Add these methods and the corresponding multi-recipients versions */

    /**
     * TODO
     * @param pageAccessToken
     * @param text
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendTextMessage(String pageAccessToken, String text, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                .setText(text)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param text
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendTextMessage(String pageAccessToken, String text, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                .setText(text)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param audioUrl
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendAudioMessage(String pageAccessToken, String audioUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.AUDIO)
                .setMediaUrl(audioUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param audioUrl
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendAudioMessage(String pageAccessToken, String audioUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.AUDIO)
                .setMediaUrl(audioUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param imageUrl
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendImageMessage(String pageAccessToken, String imageUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.IMAGE)
                .setMediaUrl(imageUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param imageUrl
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendImageMessage(String pageAccessToken, String imageUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.IMAGE)
                .setMediaUrl(imageUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param videoUrl
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendVideoMessage(String pageAccessToken, String videoUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.VIDEO)
                .setMediaUrl(videoUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param videoUrl
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendVideoMessage(String pageAccessToken, String videoUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.VIDEO)
                .setMediaUrl(videoUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param fileUrl
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendFileMessage(String pageAccessToken, String fileUrl, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.FILE)
                .setMediaUrl(fileUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param fileUrl
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendFileMessage(String pageAccessToken, String fileUrl, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.FILE)
                .setMediaUrl(fileUrl)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param action
     * @param recipientId
     * @return
     */
    public static SendMessageResponse sendAction(String pageAccessToken, OutgoingMessage.SenderAction action, String recipientId) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.SENDER_ACTION)
                .setSenderAction(action)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutGoingRecipient(recipientId));
    }

    /**
     * TODO
     * @param pageAccessToken
     * @param action
     * @param recipientIds
     * @return
     */
    public static SendMessageResponse[] sendAction(String pageAccessToken, OutgoingMessage.SenderAction action, String[] recipientIds) {
        // Create the message
        OutgoingMessage message = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.SENDER_ACTION)
                .setSenderAction(action)
                .build();

        // Send the message
        return sendMessage(pageAccessToken, message, getOutgoingRecipients(recipientIds));
    }

    /**
     * Create an OutgoingRecipient object starting with a Recipient ID.
     *
     * @param recipientId The Recipient ID.
     * @return The resulting OutgoingRecipient.
     */
    private static OutgoingMessage.OutgoingRecipient getOutGoingRecipient(String recipientId) {
        return new OutgoingMessage.OutgoingRecipient(null, recipientId);
    }

    /**
     * Create an array of OutgoingRecipient objects starting with an array of Recipient IDs.
     *
     * @param recipients The Recipient IDs.
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

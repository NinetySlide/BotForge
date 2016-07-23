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

package com.ninetyslide.libs.feta.adapter;

import com.google.gson.*;
import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.core.BotContext;
import com.ninetyslide.libs.feta.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.feta.core.message.outgoing.response.SendMessageError;
import com.ninetyslide.libs.feta.core.message.outgoing.response.SendMessageResponse;
import com.ninetyslide.libs.feta.core.message.outgoing.response.SendMessageSuccess;
import com.ninetyslide.libs.feta.util.GsonManager;
import com.ninetyslide.libs.feta.util.NetworkManager;

/**
 * Class that provides the facilities to access the Send API.
 */
public class SendMessageAdapter {

    private final static String SEND_MESSAGE_BASE_URL = "https://graph.facebook.com/v2.6/me/messages?access_token=";

    private static Gson gson = GsonManager.getGsonInstance();
    private static JsonParser jsonParser = GsonManager.getJsonParserInstance();

    /**
     * Method used to send a message from a certain bot.
     *
     * @param context The bot context to use for message send.
     * @param message The message to send.
     * @return A SendMessageSuccess instance or a SendMessageError instance, if something went wrong.
     */
    public static SendMessageResponse sendMessage(BotContext context, OutgoingMessage message) {
        // Perform the request
        String response = NetworkManager.performPostRequest(
                SEND_MESSAGE_BASE_URL + context.getPageAccessToken(),
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
            // Create and return an error in case of network problem
            SendMessageError error = new SendMessageError();

            error.setCode(SendMessageError.NETWORK_ERROR_CODE);
            error.setType(SendMessageError.NETWORK_ERROR_TYPE);
            error.setMessage(SendMessageError.NETWORK_ERROR_MESSAGE);
            error.setFbtraceId(SendMessageError.NETWORK_ERROR_FBTRACE);

            return error;
        }
    }
    // TODO: Allow multiple recipient for a message by creating a new method here and by marking the message as multiple recipient capable in the message builder class
}

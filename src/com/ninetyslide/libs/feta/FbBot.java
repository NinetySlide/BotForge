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

package com.ninetyslide.libs.feta;

import com.google.gson.*;
import com.ninetyslide.libs.feta.core.BotContext;
import com.ninetyslide.libs.feta.core.message.incoming.*;
import com.ninetyslide.libs.feta.util.BotContextManager;
import com.ninetyslide.libs.feta.util.SignatureVerifier;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.ninetyslide.libs.feta.common.Constants.*;

/**
 * Main class that handles the Bot. This class is an HttpServlet that receives GET and POST HTTP calls, extract the
 * messages and delivers them to a series of callbacks, each one of them for a specific event.
 *
 * A Bot should override one or more of these callbacks to handle the events and the received messages, otherwise they
 * will just be ignored (default behaviour).
 *
 * Please note that a single POST HTTP call can carry a batch of messages. When this happens, the messages will be
 * parsed sequentially, triggering a callback invocation for each message. So, you are advised to not perform heavy
 * load work inside the callbacks to avoid slowing down message processing. If you need to perform heavy computation
 * for every message received, it is recommended to spawn a different thread (if your environment allows you to do so)
 * or to use some sort of task queue.
 */
public abstract class FbBot extends HttpServlet {

    private Gson gson;
    private JsonParser parser;

    protected BotContextManager contextManager;

    /**
     * Method that creates the BotContext object starting from parameters set in the deployment descriptor and then
     * invoke the botInit() method to let the Bot perform its specific initialization.
     *
     * @param config The config object used to retrieve the parameters.
     * @throws ServletException When there is a Servlet related error.
     */
    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Initialize all the fields
        gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();
        parser = new JsonParser();
        contextManager = BotContextManager.getInstance();

        // Call the method for Bot-specific initialization
        List<BotContext> contexts = botInit();

        // Add all returned contexts to the context manager, if any
        if (contexts != null && !contexts.isEmpty()) {
            for (BotContext context : contexts) {
                contextManager.addContext(context);
            }
        }
    }

    /**
     * This method is only used to receive Webhook Validations. It retrieves the BotContext using the request URL and
     * uses the Verify Token associated with the context to match the one provided in the request. In case of matching,
     * it sends back the value of the "challenge" parameter.
     *
     * @param req The request object.
     * @param resp The response object.
     * @throws ServletException When there is a Servlet related error.
     * @throws IOException When there is an I/O error.
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Retrieve the values from the request
        String webhookUrl = req.getRequestURL().toString();
        String mode = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_MODE);
        String verifyToken = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_VERIFY_TOKEN);
        String challenge = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_CHALLENGE);

        // Retrieve the context or fail if the context is not found
        BotContext context = retrieveContext(null, webhookUrl);
        if (context == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check whether the mode is right and the token match
        if (WEBHOOK_VALIDATION_MODE_SUBSCRIBE.equals(mode) &&
                context.getVerifyToken().equals(verifyToken) &&
                challenge != null) {

            // Set the HTTP Headres
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(HTTP_CONTENT_TYPE_TEXT);
            resp.setCharacterEncoding(HTTP_CHAR_ENCODING);

            // Write the challenge back to Facebook
            PrintWriter respWriter = resp.getWriter();
            respWriter.write(challenge);
            respWriter.flush();
            respWriter.close();

        } else {
            // Send back an error in case something went wrong
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * This method handles all the callbacks headed to the Webhook other than the Webhook Validation. It retrieves the
     * BotContext using the request URL, verifies the signature of the request (if enabled), parses the message received
     * via the Webhook and then delivers the parsed message to the right callback, depending on the message type.
     *
     * @param req The request object.
     * @param resp The response object.
     * @throws ServletException When there is a Servlet related error.
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        // TODO: Handle the reception of the like button
        // TODO: Handle the sending of location message (if possible)
        // Get the URL of the request
        String webhookUrl = req.getRequestURL().toString();

        // Retrieve the context or fail if the context is not found
        BotContext context = retrieveContext(null, webhookUrl);
        if (context == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get the signature header
        String signatureHeader = req.getHeader(HTTP_HEADER_SIGNATURE);

        // Get the JSON String
        String jsonStr;
        try {
            jsonStr = extractJsonString(req.getReader());
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Verify the signature using HMAC-SHA1 and send back an error if verification fails
        if (context.isCallbacksValidationActive() &&
                !SignatureVerifier.verifySignature(jsonStr, signatureHeader, context.getAppSecretKey())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        // Parse the JSON String
        JsonObject rawMessage = parser.parse(jsonStr).getAsJsonObject();

        // Process every message of the batch
        JsonArray entries = rawMessage.getAsJsonArray(JSON_CALLBACK_FIELD_NAME_ENTRY);

        for (JsonElement rawEntry : entries) {

            JsonObject entry = rawEntry.getAsJsonObject();
            JsonArray messages = entry.getAsJsonArray(JSON_CALLBACK_FIELD_NAME_MESSAGING);

            for (JsonElement messageRaw : messages) {
                JsonObject message = messageRaw.getAsJsonObject();
                JsonObject content;
                IncomingMessage incomingMessage;

                if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_MESSAGE)) != null) {

                    // It's a message received, parse it correctly based on the sub type
                    if (content.getAsJsonObject(JSON_CALLBACK_SUB_TYPE_NAME_TEXT) != null) {
                        incomingMessage = gson.fromJson(content, IncomingTextMessage.class);
                    } else if (content.getAsJsonArray(JSON_CALLBACK_SUB_TYPE_NAME_ATTACHMENTS) != null) {
                        incomingMessage = gson.fromJson(content, IncomingAttachmentMessage.class);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the right callback based on the type
                    ReceivedMessage receivedMessage = (ReceivedMessage) incomingMessage;
                    if (receivedMessage.isEcho()) {
                        onMessageEchoReceived(context, receivedMessage);
                    } else {
                        onMessageReceived(context, receivedMessage);
                    }

                } else if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_POSTBACK)) != null) {

                    // Parse the message as a postback message
                    incomingMessage = gson.fromJson(content, Postback.class);

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the postback callback
                    onPostbackReceived(context, (Postback) incomingMessage);

                } else if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_OPTIN)) != null) {

                    // Parse the message as an authentication callback
                    incomingMessage = gson.fromJson(content, Optin.class);

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the authentication callback
                    onAuthenticationReceived(context, (Optin) incomingMessage);

                } else if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_ACCOUNT_LINKING)) != null) {

                    // Parse the message as an account linking callback
                    incomingMessage = gson.fromJson(content, AccountLinking.class);

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the account linking callback
                    onAccountLinkingReceived(context, (AccountLinking) incomingMessage);

                } else if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_DELIVERY)) != null) {

                    // Parse the message as a delivery receipt
                    incomingMessage = gson.fromJson(content, DeliveryReceipt.class);

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the message delivery callback
                    onMessageDelivered(context, (DeliveryReceipt) incomingMessage);

                } else if ((content = message.getAsJsonObject(JSON_CALLBACK_TYPE_NAME_READ)) != null) {

                    // Parse the message as a read receipt
                    incomingMessage = gson.fromJson(content, ReadReceipt.class);

                    // Set Sender ID, Recipient ID and Timestamp
                    setMessageHeaders(rawMessage, incomingMessage);

                    // Deliver the message to the message read callback
                    onMessageRead(context, (ReadReceipt) incomingMessage);

                }
            }
        }

        // Answer with HTTP Code 200 if there were no errors during the message processing
        resp.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Retrieve the context using one of either pageId or webhook, and falling back to invocation of onContextLoad if
     * the context is not present in the context manager.
     *
     * @param pageId The Page ID associated with the context.
     * @param webhookUrl The Webhook URL associated with the context.
     * @return The context retrieved or null if the context was not found.
     */
    private BotContext retrieveContext(String pageId, String webhookUrl) {
        String contextKey;

        // Decide which variable to use as the context key
        if (pageId != null) {
            contextKey = pageId;
        } else if (webhookUrl != null) {
            contextKey = webhookUrl;
        } else {
            return null;
        }

        // Retrieve the context from the context manager or invoke onContextLoad
        if (contextManager.containsContext(contextKey)) {
            return contextManager.getContext(contextKey);
        } else {
            return onContextLoad(pageId, webhookUrl);
        }
    }

    /**
     * Read the JSON String from the request body.
     *
     * @param jsonReader The BufferedReader from the request.
     * @return The String representing the JSON.
     * @throws IOException When there is an I/O error.
     */
    private String extractJsonString(BufferedReader jsonReader) throws IOException {
        String jsonPartial;
        StringBuilder jsonRaw = new StringBuilder();

        while ((jsonPartial = jsonReader.readLine()) != null) {
            jsonRaw.append(jsonPartial);
        }

        return jsonRaw.toString();
    }

    /**
     * Set the Sender ID, Recipient ID and Timestamp in the incoming message.
     *
     * @param rawMessage The raw JSON Object received via the callback.
     * @param message The extracted IncomingMessage.
     * @return The IncomingMessage passed as input, with the values set.
     */
    private IncomingMessage setMessageHeaders(JsonObject rawMessage, IncomingMessage message) {

        message.setSenderId(
                rawMessage
                        .getAsJsonObject(JSON_CALLBACK_FIELD_NAME_SENDER)
                        .get(JSON_CALLBACK_FIELD_NAME_ID)
                        .getAsString()
        );

        message.setRecipientId(
                rawMessage
                        .getAsJsonObject(JSON_CALLBACK_FIELD_NAME_RECIPIENT)
                        .get(JSON_CALLBACK_FIELD_NAME_ID)
                        .getAsString()
        );

        message.setTimestamp(
                rawMessage
                        .get(JSON_CALLBACK_FIELD_NAME_TIMESTAMP)
                        .getAsLong()
        );

        return message;

    }

    /**
     * Method invoked only once, when the Bot is first initialized, to perform some custom Bot-specific initializations
     * and to bulk load BotContext object inside the BotContextManager. The default implementation just returns null.
     * The override of this method is optional.
     *
     * Initializations aside, this is the perfect place to bulk load BotContext objects without relying on the lazy
     * loading performed when a context is first needed. This method is called only once when the Bot is first
     * initialized.
     *
     * @return A list of BotContext objects to add to the BotContextManager.
     */
    protected List<BotContext> botInit() {
        return null;
    }

    /**
     * Callback invoked when a Text or Attachment message is received. To access all the information, the message type
     * shall be inspected and the message passed as an argument shall be cast appropriately. The parameters contain
     * everything is needed to perform actions in response to the event. The default implementation does just nothing.
     * The overriding of this method is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onMessageReceived(BotContext context, ReceivedMessage message) {}

    /**
     * Callback invoked when a Postback message is received. The parameters contain everything is needed to perform
     * actions in response to the event. The default implementation does just nothing. The overriding of this method
     * is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onPostbackReceived(BotContext context, Postback message) {}

    /**
     * Callback invoked when an Authentication (Optin) message is received. The parameters contain everything is
     * needed to perform actions in response to the event. The default implementation does just nothing. The overriding
     * of this method is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onAuthenticationReceived(BotContext context, Optin message) {}

    /**
     * Callback invoked when a Delivery Confirmation message is received. The parameters contain everything is needed
     * to perform actions in response to the event. The default implementation does just nothing. The overriding of
     * this method is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onMessageDelivered(BotContext context, DeliveryReceipt message) {}

    /**
     * Callback invoked when a Read Confirmation message is received. The parameters contain everything is needed to
     * perform actions in response to the event. The default implementation does just nothing. The overriding of this
     * method is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onMessageRead(BotContext context, ReadReceipt message) {}

    /**
     * Callback invoked when a Message Echo is received. The parameters contain everything is needed to perform
     * actions in response to the event. The default implementation does just nothing. The overriding of this method
     * is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onMessageEchoReceived(BotContext context, ReceivedMessage message) {}

    /**
     * Callback invoked when an Account Linking message is received. The parameters contain everything is needed to
     * perform actions in response to the event. The default implementation does just nothing. The overriding of this
     * method is optional.
     *
     * @param context The context of the Bot associated with this request.
     * @param message The message received via the Webhook.
     */
    protected void onAccountLinkingReceived(BotContext context, AccountLinking message) {}

    /**
     * Callback invoked when the context is not found inside the BotContextManager. This gives the chance to lazy load
     * the contexts. Please note that only one of the two parameters will be set at invocation time. They will never be
     * both set at the same time. The implementation must be prepared to work with either one of the parameter is set
     * at invocation time. The implementation of this method is mandatory.
     *
     * Please note that a BotContext can be loaded, removed and modified anytime inside the BotContextManager just by
     * using the BotContextManager instance provided as a field of this class.
     *
     * @param pageId The Page ID associated with the context.
     * @param webhookUrl The webhookUrl associated with the context.
     * @return The context associated with the identifiers passed as arguments.
     */
    protected abstract BotContext onContextLoad(String pageId, String webhookUrl);

}

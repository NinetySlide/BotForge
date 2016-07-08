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

import static com.ninetyslide.libs.feta.common.Constants.*;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.ninetyslide.libs.feta.bean.BotContext;
import com.ninetyslide.libs.feta.utils.BotContextManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Main class that handles the Bot. This class is an HttpServlet that receives GET and POST HTTP calls,
 */
public abstract class FbBot extends HttpServlet {

    private Gson gson;
    private JsonParser parser;

    protected BotContextManager contextManager;

    /**
     * Method that creates the BotContext object starting from parameters set in the deployment descriptor and then
     * invoke the botinit() method to let the Bot perform its specific initialization.
     *
     * @param config The config object used to retrieve the parameters.
     * @throws ServletException
     */
    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Initialize all the fields
        gson = new Gson();
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
     * @throws ServletException
     * @throws IOException
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
     * TODO
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: Add actual implementation

        // Get the URL of the request
        String webhookUrl = req.getRequestURL().toString();

        // Get the header signature
        String signatureHeader = req.getHeader(HTTP_HEADER_SIGNATURE);

        // Get the JSON String TODO: Extract into a method
        String jsonPartial;
        StringBuffer jsonRaw = new StringBuffer();
        BufferedReader jsonReader = req.getReader();

        while ((jsonPartial = jsonReader.readLine()) != null) {
            jsonRaw.append(jsonPartial);
        }
        String jsonStr = jsonRaw.toString();

        // Verify the signature using HMAC-SHA1

        // Parse the JSON String
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
     * Method that can be overridden (not mandatory) to perform some Bot-specific initialization and contexts loading.
     * It is called only once when the Bot is first initialized.
     *
     * @return A list of BotContext objects to add to the BotContextManager.
     */
    protected List<BotContext> botInit() {
        return null;
    }

    /**
     * Callback invoked when the context is not found inside the BotContextManager. This gives the chance to lazy load
     * the contexts. Please note that only one of the two parameters will be set at invocation time. They will never be
     * both set at the same time. The implementation must be prepared to work with either one of the parameter is set
     * at invocation time.
     *
     * @param pageId The Page ID associated with the context.
     * @param webhookUrl The webhookUrl associated with the context.
     * @return The context associated with the identifiers passed as arguments.
     */
    protected abstract BotContext onContextLoad(String pageId, String webhookUrl);

    protected void onMessageReceived() {} // TODO Add the right parameters

    protected void onPostbackReceived() {} // TODO Add the right parameters

    protected void onAuthenticationReceived() {} // TODO Add the right parameters

    protected void onMessageDelivered() {} // TODO Add the right parameters

    protected void onMessageRead() {} // TODO Add the right parameters

    protected void onMessageEchoReceived() {} // TODO Add the right parameters

    protected void onAccountLinkingReceived() {} // TODO Add the right parameters

}

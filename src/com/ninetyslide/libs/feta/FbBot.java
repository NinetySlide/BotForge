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

import com.ninetyslide.libs.feta.bean.BotContext;
import static com.ninetyslide.libs.feta.common.Constants.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Main class that handles the Bot. This class is an HttpServlet that receives GET and POST HTTP calls,
 */
public abstract class FbBot extends HttpServlet {

    private BotContext botContext;

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

        // Retrieve the parameters
        String appSecretKey = config.getInitParameter(PARAM_NAME_APP_SECRET_KEY);
        String pageAccessToken = config.getInitParameter(PARAM_NAME_PAGE_ACCESS_TOKEN);
        String verifyToken = config.getInitParameter(PARAM_NAME_VERIFY_TOKEN);
        String validateCallbacks = config.getInitParameter(PARAM_NAME_VALIDATE_CALLBACKS);

        // Create and set the context of the Bot
        botContext = new BotContext(
                pageAccessToken,
                appSecretKey,
                verifyToken,
                validateCallbacks
        );

        // Call the method for Bot-specific initialization
        botInit();
    }

    /**
     * This method is only used to receive Webhook Validations. It uses the locally saved Verify Token to match the
     * provided one. In case of matching, it sends back the value of the "challenge" parameter.
     *
     * @param req The request object.
     * @param resp The response object.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Retrieve the values from the request
        String mode = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_MODE);
        String verifyToken = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_VERIFY_TOKEN);
        String challenge = req.getParameter(WEBHOOK_VALIDATION_PARAM_NAME_CHALLENGE);

        // Check whether the mode is right and the token match
        if (WEBHOOK_VALIDATION_MODE_SUBSCRIBE.equals(mode) &&
                botContext.getVerifyToken().equals(verifyToken) &&
                challenge != null) {

            // Set the HTTP Headres
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(CONTENT_TYPE_TEXT);
            resp.setCharacterEncoding(CHAR_ENCODING);

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
    }

    /**
     * Method that can be overridden to perform some Bot-specific initialization. It is called only once when the Bot
     * is first initialized.
     */
    protected void botInit() {
    }

    protected abstract void onMessageReceived(); // TODO Add the right parameters

    protected abstract void onPostbackReceived(); // TODO Add the right parameters

    protected abstract void onAuthenticationReceived(); // TODO Add the right parameters

    protected abstract void onMessageDelivered(); // TODO Add the right parameters

    protected abstract void onMessageRead(); // TODO Add the right parameters

    protected abstract void onMessageEchoReceived(); // TODO Add the right parameters

    protected abstract void onAccountLinkingReceived(); // TODO Add the right parameters

}

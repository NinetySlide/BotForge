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
import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.exception.BotInitParameterMissingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Main class that handles the Bot. This class is an HttpServlet that receives GET and POST HTTP calls,
 */
public abstract class FbBot extends HttpServlet {

    private BotContext botContext;

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);

        // TODO Add implementation
        // Retrieve the parameters
        String appSecretKey = config.getInitParameter(Constants.PARAM_NAME_APP_SECRET_KEY);
        String pageAccessToken = config.getInitParameter(Constants.PARAM_NAME_PAGE_ACCESS_TOKEN);
        String verifyToken = config.getInitParameter(Constants.PARAM_NAME_VERIFY_TOKEN);
        String validateCallbacks = config.getInitParameter(Constants.PARAM_NAME_VALIDATE_CALLBACKS);

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
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

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

package com.ninetyslide.libs.botforge.core;

import com.ninetyslide.libs.botforge.exception.BotInitParameterMissingException;

import static com.ninetyslide.libs.botforge.common.Constants.*;

/**
 * Class representing the context of a Bot. Contains the values and the parameters that are used across all the Bot,
 * such as IDs and Keys.
 */
public final class BotContext {

    private String pageId = null;
    private String pageAccessToken = null;
    private String appSecretKey = null;
    private String verifyToken = null;
    private String webhookUrl = null;
    private boolean validateCallbacks = true;
    private boolean debug = false;

    /**
     * Build a new Bot Context setting all the passed values as fields of the class. Please note that callback
     * validation is enabled by default and debug is disabled by default.
     *
     * @param pageId The Page ID.
     * @param pageAccessToken The Page Access Token for the Bot.
     * @param appSecretKey The bot's App Secret Key.
     * @param verifyToken The Verify Token used to authenticate the webhook.
     * @param webhookUrl The URL of the webhook.
     */
    public BotContext(String pageId, String pageAccessToken, String appSecretKey, String verifyToken, String webhookUrl) {
        this(pageId, pageAccessToken, appSecretKey, verifyToken, webhookUrl, true, false);
    }

    /**
     * Build a new Bot Context setting all the passed values as fields of the class. Please note that debug is disabled
     * by default.
     *
     * @param pageId The Page ID.
     * @param pageAccessToken The Page Access Token for the Bot.
     * @param appSecretKey The bot's App Secret Key.
     * @param verifyToken The Verify Token used to authenticate the webhook.
     * @param webhookUrl The URL of the webhook.
     * @param validateCallbacks Whether the callbacks to the webhook must be validated. This should be always set to
     *                          true (the default value) unless you have an extremely valid reason not to do so.
     */
    public BotContext(String pageId, String pageAccessToken, String appSecretKey, String verifyToken, String webhookUrl, boolean validateCallbacks) {
        this(pageId, pageAccessToken, appSecretKey, verifyToken, webhookUrl, validateCallbacks, false);
    }

    /**
     * Build a new Bot Context setting all the passed values as fields of the class.
     *
     * @param pageId The Page ID.
     * @param pageAccessToken The Page Access Token for the Bot.
     * @param appSecretKey The bot's App Secret Key.
     * @param verifyToken The Verify Token used to authenticate the webhook.
     * @param webhookUrl The URL of the webhook.
     * @param validateCallbacks Whether the callbacks to the webhook must be validated. This should be always set to
     *                          true (the default value) unless you have an extremely valid reason not to do so.
     * @param debug Whether the debug mode must be enabled. When it is enabled, all the received message data will be
     *              logged.
     */
    public BotContext(String pageId, String pageAccessToken, String appSecretKey, String verifyToken, String webhookUrl, boolean validateCallbacks, boolean debug) {

        if (pageId == null || "".equals(pageId)) {
            throw new BotInitParameterMissingException(MSG_PARAM_MISSING_PAGE_ID);
        } else {
            this.pageId = pageId;
        }

        if (appSecretKey == null || "".equals(appSecretKey)) {
            throw new BotInitParameterMissingException(MSG_PARAM_MISSING_APP_SECRET_KEY);
        } else {
            this.appSecretKey = appSecretKey;
        }

        if (pageAccessToken == null || "".equals(pageAccessToken)) {
            throw new BotInitParameterMissingException(MSG_PARAM_MISSING_PAGE_ACCESS_TOKEN);
        } else {
            this.pageAccessToken = pageAccessToken;
        }

        if (verifyToken == null || "".equals(verifyToken)) {
            throw new BotInitParameterMissingException(MSG_PARAM_MISSING_VERIFY_TOKEN);
        } else {
            this.verifyToken = verifyToken;
        }

        if (webhookUrl == null || "".equals(webhookUrl)) {
            throw new BotInitParameterMissingException(MSG_PARAM_MISSING_WEBHHOK_URL);
        } else {
            this.webhookUrl = webhookUrl;
        }

        this.validateCallbacks = validateCallbacks;

        this.debug = debug;

    }

    public String getPageId() {
        return pageId;
    }

    public String getPageAccessToken() {
        return pageAccessToken;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public String getVerifyToken() {
        return verifyToken;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public boolean isCallbacksValidationActive() {
        return validateCallbacks;
    }

    public boolean isDebugEnabled() {
        return debug;
    }
}

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

package com.ninetyslide.libs.feta.bean;

import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.exception.BotInitParameterMissingException;

/**
 * This represents the context of a Bot. It contains the values and the parameters that are used across all the Bot,
 * such as IDs and Keys.
 */
public class BotContext {

    private String pageAccessToken;
    private String appSecretKey;
    private String verifyToken;
    private boolean validateCallbacks;

    public BotContext(String pageAccessToken, String appSecretKey, String verifyToken, String validateCallbacksStr) {

        if (appSecretKey == null) {
            throw new BotInitParameterMissingException(Constants.MSG_PARAM_MISSING_APP_SECRET_KEY);
        } else {
            this.appSecretKey = appSecretKey;
        }

        if (pageAccessToken == null) {
            throw new BotInitParameterMissingException(Constants.MSG_PARAM_MISSING_PAGE_ACCESS_TOKEN);
        } else {
            this.pageAccessToken = pageAccessToken;
        }

        if (verifyToken == null) {
            throw new BotInitParameterMissingException(Constants.MSG_PARAM_MISSING_VERIFY_TOKEN);
        } else {
            this.verifyToken = verifyToken;
        }

        if ("true".equalsIgnoreCase(validateCallbacksStr)) {
            validateCallbacks = true;
        } else if ("false".equalsIgnoreCase(validateCallbacksStr)) {
            validateCallbacks = false;
        } else {
            throw new BotInitParameterMissingException(Constants.MSG_PARAM_MISSING_VALIDATE_CALLBACKS);
        }

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

    public boolean isCallbacksValidationActive() {
        return validateCallbacks;
    }

}

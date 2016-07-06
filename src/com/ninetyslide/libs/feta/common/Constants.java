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

package com.ninetyslide.libs.feta.common;

/**
 * Constants class. This holds all the constants needed in the Bot.
 */
public class Constants {

    public final static String PARAM_NAME_PAGE_ACCESS_TOKEN = "pageAccessToken";
    public final static String PARAM_NAME_APP_SECRET_KEY = "appSecretKey";
    public final static String PARAM_NAME_VERIFY_TOKEN = "verifyToken";
    public final static String PARAM_NAME_VALIDATE_CALLBACKS = "validateCallbacks";

    public final static String PARAM_MISSING_MSG_PAGE_ACCESS_TOKEN = "Page Access Token parameter is missing. Please verify your init parameters.";
    public final static String PARAM_MISSING_MSG_APP_SECRET_KEY = "App Secret Key parameter is missing. Please verify your init parameters.";
    public final static String PARAM_MISSING_MSG_VERIFY_TOKEN = "Verify Token parameter is missing. Please verify your init parameters.";
    public final static String PARAM_MISSING_MSG_VALIDATE_CALLBACKS = "Validate Callbacks parameter is missing or not in the true/false form. Please verify your init parameters.";

}

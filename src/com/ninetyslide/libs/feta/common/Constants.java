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

    // Constants related to Servlet init parameters
    public final static String PARAM_NAME_PAGE_ACCESS_TOKEN = "pageAccessToken";
    public final static String PARAM_NAME_APP_SECRET_KEY = "appSecretKey";
    public final static String PARAM_NAME_VERIFY_TOKEN = "verifyToken";
    public final static String PARAM_NAME_VALIDATE_CALLBACKS = "validateCallbacks";

    // Constants related to BotInitParameterMissingException error messages
    public final static String MSG_PARAM_MISSING_PAGE_ID = "Page ID parameter is missing. Please verify your context parameters.";
    public final static String MSG_PARAM_MISSING_PAGE_ACCESS_TOKEN = "Page Access Token parameter is missing. Please verify your context parameters.";
    public final static String MSG_PARAM_MISSING_APP_SECRET_KEY = "App Secret Key parameter is missing. Please verify your context parameters.";
    public final static String MSG_PARAM_MISSING_WEBHHOK_URL = "Webhook URL parameter is missing. Please verify your context parameters.";
    public final static String MSG_PARAM_MISSING_VERIFY_TOKEN = "Verify Token parameter is missing. Please verify your context parameters.";

    // Constants related to various exceptions
    public final static String MSG_NOTIFICATION_TYPE_INVALID = "Notification type can only be one of: regular, silent push and no push.";
    public final static String MSG_RECIPIENT_INVALID = "Exactly one of phone number or ID must be set as a recipient.";
    public final static String MSG_MESSAGE_OPERATION_INVALID = "The message type does not support this operation.";
    public final static String MSG_TEXT_LENGTH_EXCEEDED = "The text exceed the allowed character limit.";

    // Various error messages
    public final static String MSG_INVALID_CONTEXT = "Invalid context passed as an argument";

    // Constants related to HTTP parameters
    public final static String HTTP_CONTENT_TYPE_TEXT = "text/html";
    public final static String HTTP_CONTENT_TYPE_JSON = "application/json";
    public final static String HTTP_CHAR_ENCODING = "UTF-8";
    public final static String HTTP_HEADER_SIGNATURE = "x-hub-signature";

    // Constants related to Webhook Validation
    public final static String WEBHOOK_VALIDATION_PARAM_NAME_MODE = "hub.mode";
    public final static String WEBHOOK_VALIDATION_PARAM_NAME_VERIFY_TOKEN = "hub.verify_token";
    public final static String WEBHOOK_VALIDATION_PARAM_NAME_CHALLENGE = "hub.challenge";
    public final static String WEBHOOK_VALIDATION_MODE_SUBSCRIBE = "subscribe";

    // Constants related to JSON parsing
    public final static String JSON_CALLBACK_FIELD_NAME_ENTRY = "entry";
    public final static String JSON_CALLBACK_FIELD_NAME_MESSAGING = "messaging";
    public final static String JSON_CALLBACK_FIELD_NAME_SENDER = "sender";
    public final static String JSON_CALLBACK_FIELD_NAME_RECIPIENT = "recipient";
    public final static String JSON_CALLBACK_FIELD_NAME_TIMESTAMP = "timestamp";
    public final static String JSON_CALLBACK_FIELD_NAME_ID = "id";
    public final static String JSON_CALLBACK_TYPE_NAME_MESSAGE = "message";
    public final static String JSON_CALLBACK_TYPE_NAME_POSTBACK = "postback";
    public final static String JSON_CALLBACK_TYPE_NAME_OPTIN = "optin";
    public final static String JSON_CALLBACK_TYPE_NAME_ACCOUNT_LINKING = "account_linking";
    public final static String JSON_CALLBACK_TYPE_NAME_DELIVERY = "delivery";
    public final static String JSON_CALLBACK_TYPE_NAME_READ = "read";
    public final static String JSON_CALLBACK_SUB_TYPE_NAME_TEXT = "text";
    public final static String JSON_CALLBACK_SUB_TYPE_NAME_ATTACHMENTS = "attachments";

    // Constants related to limits
    public final static int LIMIT_TEXT_LENGTH = 320;

}

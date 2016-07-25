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

package com.ninetyslide.libs.feta.core.message.outgoing.widget;

import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.ValidityChecker;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

/**
 * Class representing a Button widget that can be added to multiple templates.
 */
public final class Button implements ValidityChecker {

    private final static String TYPE_WEB_URL = "web_url";
    private final static String TYPE_POSTBACK = "postback";
    private final static String TYPE_PHONE_NUMBER = "phone_number";

    private transient ButtonType buttonType = null;
    private String type = null;
    private String title = null;
    private String url = null;
    private String payload = null;

    private Button() {
    }

    public Button(ButtonType buttonType) {
        this.buttonType = buttonType;

        // Set the correct button type
        switch (this.buttonType) {
            case WEB_URL:
                type = TYPE_WEB_URL;
                break;
            case POSTBACK:
                type = TYPE_POSTBACK;
                break;
            case PHONE_NUMBER:
                type = TYPE_PHONE_NUMBER;
                break;
            default:
                throw new IllegalArgumentException(Constants.MSG_BUTTON_TYPE_INVALID);
        }
    }

    /**
     * Set the title for the button. Same as setTitle(title, false).
     *
     * @param title The title to set to the button.
     * @return The instance of the button used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public Button setTitle(String title) throws TextLengthExceededException {
        return setTitle(title, false);
    }

    /**
     * Set the title for the button. Please note that at the time of this version, the length of the title is limited
     * to 20 characters. If you exceed this limit, an exception will be thrown. However, if you know what you are doing,
     * you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param title The title to set to the button.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the button used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to
     * false.
     */
    public Button setTitle(String title, boolean force) throws TextLengthExceededException {
        if (title != null) {
            // Title has 20 character limit
            if (!force && title.length() > Constants.LIMIT_TITLE_LENGTH) {
                throw new TextLengthExceededException(Constants.MSG_TITLE_LENGTH_EXCEEDED);
            }

            // Set the title
            this.title = title;
        }

        return this;
    }

    /**
     * Set the URL that will be opened when the button is pressed.
     *
     * @param url The URL to assign to the button.
     * @return The instance of the button used to invoke this method.
     */
    public Button setUrl(String url) {
        if (buttonType != ButtonType.WEB_URL) {
            throw new UnsupportedOperationException(Constants.MSG_BUTTON_OPERATION_NOT_SUPPORTED);
        }
        this.url = url;

        return this;
    }

    /**
     * Set the payload for the button. Same as setPayload(payload, false).
     *
     * @param payload The payload to attach to the button.
     * @return The instance of the button used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public Button setPayload(String payload) throws TextLengthExceededException {
        return setPayload(payload, false);
    }

    /**
     * Set the payload for the button. Please note that at the time of this version, the length of the payload is
     * limited to 1000 characters. If you exceed this limit, an exception will be thrown. However, if you know what
     * you are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param payload The payload to attach to the button.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the button used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to
     * false.
     */
    public Button setPayload(String payload, boolean force) throws TextLengthExceededException {
        if (buttonType != ButtonType.PHONE_NUMBER && buttonType != ButtonType.POSTBACK) {
            throw new UnsupportedOperationException(Constants.MSG_BUTTON_OPERATION_NOT_SUPPORTED);
        }

        if (payload != null) {
            // Payload has 1000 character limit
            if (!force && payload.length() > Constants.LIMIT_PAYLOAD) {
                throw new TextLengthExceededException(Constants.MSG_PAYLOAD_LENGTH_EXCEEDED);
            }

            // Set the payload
            this.payload = payload;
        }

        return this;
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        // Type and title must be set
        if (type == null || title == null) {
            return false;
        }

        // In case of Web URL button: url must be set, payload must not
        if (buttonType == ButtonType.WEB_URL) {
            if (url == null || payload != null) {
                return false;
            }
        }

        // In case of Postback or Phone Number buttons: url must not be set, payload must be.
        if (buttonType == ButtonType.POSTBACK || buttonType == ButtonType.PHONE_NUMBER) {
            if (url != null || payload == null) {
                return false;
            }
        }

        return true;
    }

    public enum ButtonType {
        WEB_URL,
        POSTBACK,
        PHONE_NUMBER
    }
}

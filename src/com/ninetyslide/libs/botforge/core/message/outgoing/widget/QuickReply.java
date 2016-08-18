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

package com.ninetyslide.libs.botforge.core.message.outgoing.widget;

import com.ninetyslide.libs.botforge.common.Constants;
import com.ninetyslide.libs.botforge.core.message.outgoing.feature.ValidityChecker;
import com.ninetyslide.libs.botforge.exception.TextLengthExceededException;

/**
 * Class representing a quick reply. An array of quick replies can be added to every text, multimedia and
 * template message.
 */
public final class QuickReply implements ValidityChecker {
    private final static String QUICK_REPLY_CONTENT_TYPE = "text";

    private String contentType = QUICK_REPLY_CONTENT_TYPE;
    private String title = null;
    private String payload = null;

    public QuickReply() {
    }

    /**
     * Set the title for the Quick Reply. Same as setTitle(title, false).
     *
     * @param title The title to add to the Quick Reply.
     * @return The instance of the quick reply used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public QuickReply setTitle(String title) {
        return setTitle(title, false);
    }

    /**
     * Set the title for the Quick Reply. Please note that at the time of this version, the length of the title is
     * limited to 20 characters. If you exceed this limit, an exception will be thrown. However, if you know what
     * you are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param title The title to add to the Quick Reply.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the quick reply used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    public QuickReply setTitle(String title, boolean force) {
        if (title != null) {
            // Title has 20 characters limit
            if (!force && title.length() > Constants.LIMIT_TITLE_LENGTH) {
                throw new TextLengthExceededException(Constants.MSG_TITLE_LENGTH_EXCEEDED);
            }
            this.title = title;
        }

        return this;
    }

    /**
     * Set the payload for the Quick Reply. Same as setPayload(payload, false).
     *
     * @param payload The payload to add to the Quick Reply.
     * @return The instance of the quick reply used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public QuickReply setPayload(String payload) {
        return setPayload(payload, false);
    }

    /**
     * Set the payload for the Quick Reply. Please note that at the time of this version, the length of the payload
     * is limited to 1000 characters. If you exceed this limit, an exception will be thrown. However, if you know
     * what you are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param payload The payload to add to the Quick Reply.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the quick reply used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    public QuickReply setPayload(String payload, boolean force) {
        if (payload != null) {
            // Payload has 1000 characters limit
            if (!force && payload.length() > Constants.LIMIT_PAYLOAD_LENGTH) {
                throw new TextLengthExceededException(Constants.MSG_PAYLOAD_LENGTH_EXCEEDED);
            }
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
        return title != null &&
                payload != null;
    }
}

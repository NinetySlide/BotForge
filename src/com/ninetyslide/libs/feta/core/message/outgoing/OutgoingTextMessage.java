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

package com.ninetyslide.libs.feta.core.message.outgoing;

import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesCarrier;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesSetter;
import com.ninetyslide.libs.feta.exception.QuickRepliesNumberExceededException;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

/**
 * Class representing an outgoing text message.
 */
public class OutgoingTextMessage extends OutgoingMessage implements QuickRepliesSetter {

    private TextRoot message = null;

    public OutgoingTextMessage() {
        super();
        message = new TextRoot();
    }

    @Override
    public OutgoingMessageType getOutgoingMessageType() {
        return OutgoingMessageType.TEXT;
    }

    @Override
    public void addQuickReply(QuickReply quickReply) throws QuickRepliesNumberExceededException {
        addQuickReply(quickReply, false);
    }

    @Override
    public void addQuickReply(QuickReply quickReply, boolean force) throws QuickRepliesNumberExceededException {
        message.addQuickReply(quickReply, force);
    }

    /**
     * Set the text for the outgoing message. Please note that at the time of this version, the length of the text is
     * limited to 320 characters. If you exceed this limit, an exception will be thrown. However, if you know what you
     * are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param text The text that will be assigned to the message.
     * @param force Whether the character limit must be enforced.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    void setText(String text, boolean force) throws TextLengthExceededException {
        if (text != null) {
            if (!force && text.length() > Constants.LIMIT_TEXT_LENGTH) {
                throw new TextLengthExceededException(Constants.MSG_TEXT_LENGTH_EXCEEDED);
            }
            this.message.text = text;
        }
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        return super.isValid() &&
                message != null &&
                message.isValid() &&
                message.text != null;
    }

    private static class TextRoot extends QuickRepliesCarrier {
        String text = null;
    }
}

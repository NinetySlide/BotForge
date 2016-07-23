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

package com.ninetyslide.libs.feta.util;

import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.core.message.incoming.IncomingTextMessage;
import com.ninetyslide.libs.feta.core.message.incoming.ReceivedMessage;
import com.ninetyslide.libs.feta.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

/**
 * Class that exposes the facilities to seamlessly convert an incoming message in an outgoing message, so that it's
 * easy to forward received messages to other users.
 */
public class MessageConverter {

    /**
     * This method takes an IncomingMessage and converts it to an OutgoingMessage.
     *
     * @param receivedMessage The received message that must be converted.
     * @return The newly generated OutgoingMessage.
     */
    public static OutgoingMessage getOutgoingMessage(ReceivedMessage receivedMessage) {
        switch (receivedMessage.getIncomingMessageType()) {
            case TEXT:
                // Retrieve text from message
                String text = ((IncomingTextMessage) receivedMessage)
                        .getText()
                        .substring(0, Constants.LIMIT_TEXT_LENGTH);
                OutgoingMessage outgoingMessage = null;

                // Build an OutgoingMessage
                try {
                    outgoingMessage = new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                            .setText(text)
                            .build();
                } catch (TextLengthExceededException e) {
                    // Just do nothing because the substring is never beyond the limits
                }

                // Return the message
                return outgoingMessage;

            case ATTACHMENT:
                // TODO: Implement support for attachment messages
                throw new UnsupportedOperationException(Constants.MSG_OPERATION_NOT_YET_IMPLEMENTED);

            default:
                throw new IllegalArgumentException(Constants.MSG_MESSAGE_INVALID);
        }
    }

}

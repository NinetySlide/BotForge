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
import com.ninetyslide.libs.feta.core.message.incoming.IncomingAttachmentMessage;
import com.ninetyslide.libs.feta.core.message.incoming.IncomingTextMessage;
import com.ninetyslide.libs.feta.core.message.incoming.ReceivedMessage;
import com.ninetyslide.libs.feta.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

/**
 * Class that exposes the facilities to seamlessly convert an incoming message in an outgoing message, so that it's
 * easy to forward received messages to other users.
 */
public final class MessageConverter {

    private MessageConverter() {
    }

    /**
     * Take an Incoming Message and convert it to an OutgoingMessage. It only works for text, audio, image, video and
     * file message.
     *
     * @param receivedMessage The received message that must be converted.
     * @return The newly generated OutgoingMessage.
     */
    public static OutgoingMessage getOutgoingMessage(ReceivedMessage receivedMessage) {

        // Determine the type of the ReceivedMessage and perform the right actions
        switch (receivedMessage.getIncomingMessageType()) {
            case TEXT:
                // Retrieve text from message
                String text = ((IncomingTextMessage) receivedMessage)
                        .getText();

                // Make sure text does not exceed the length limit
                if (text.length() > Constants.LIMIT_TEXT_LENGTH) {
                    text = text.substring(0, Constants.LIMIT_TEXT_LENGTH);
                }

                // Build and return the OutgoingMessage
                try {
                    return new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                            .setText(text)
                            .build();
                } catch (TextLengthExceededException e) {
                    // Just do nothing because the substring is never beyond the limits
                    return null;
                }

            case ATTACHMENT:
                // Retrieve the first attachment of the Incoming Message
                IncomingAttachmentMessage.IncomingAttachment attachment = ((IncomingAttachmentMessage) receivedMessage).getAttachment(0);

                // Determine the type of the attachment and return the right Outgoing Message
                switch (attachment.getAttachmentType()) {
                    case AUDIO:
                        return buildMediaMessage(OutgoingMessage.OutgoingMessageType.AUDIO, attachment);
                    case IMAGE:
                        return buildMediaMessage(OutgoingMessage.OutgoingMessageType.IMAGE, attachment);
                    case VIDEO:
                        return buildMediaMessage(OutgoingMessage.OutgoingMessageType.VIDEO, attachment);
                    case FILE:
                        return buildMediaMessage(OutgoingMessage.OutgoingMessageType.FILE, attachment);
                    default:
                        try {
                            return new OutgoingMessage.Builder(OutgoingMessage.OutgoingMessageType.TEXT)
                                    .setText(Constants.MSG_MESSAGE_TYPE_NOT_SUPPORTED_YET)
                                    .build();
                        } catch (TextLengthExceededException e) {
                            // Just do nothing because the substring is never beyond the limits
                            return null;
                        }
                }

            default:
                throw new IllegalArgumentException(Constants.MSG_MESSAGE_INVALID);
        }
    }

    /**
     * Generate a new multimedia Outgoing Message of the specified type, setting the media url retrieved from the
     * specified attachment.
     *
     * @param messageType The type of Outgoing Message to build.
     * @param attachment The attachment of the Received Message.
     * @return A newly generated Outgoing Message.
     */
    private static OutgoingMessage buildMediaMessage(OutgoingMessage.OutgoingMessageType messageType, IncomingAttachmentMessage.IncomingAttachment attachment) {
        return new OutgoingMessage.Builder(messageType)
                .setMediaUrl(attachment.getUrl())
                .build();
    }

}

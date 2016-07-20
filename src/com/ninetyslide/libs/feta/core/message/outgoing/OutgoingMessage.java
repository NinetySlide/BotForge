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
import com.ninetyslide.libs.feta.exception.InvalidNotificationTypeException;
import com.ninetyslide.libs.feta.exception.InvalidRecipientException;

/**
 * Abstract class for all the outgoing messages.
 */
public abstract class OutgoingMessage {

    public final static String NOTIFICATION_TYPE_REGULAR = "REGULAR";
    public final static String NOTIFICATION_TYPE_SILENT = "SILENT_PUSH";
    public final static String NOTIFICATION_TYPE_NO_PUSH = "NO_PUSH";

    public final static String QUICK_REPLY_CONTENT_TYPE = "text";

    private OutgoingRecipient recipient;
    private String notificationType;

    public OutgoingMessage() {
        try {
            setNotificationType(NOTIFICATION_TYPE_REGULAR);
        } catch (InvalidNotificationTypeException e) {
            // Just do nothing, since the exception can never be thrown.
        }
    }

    public OutgoingRecipient getRecipient() {
        return recipient;
    }

    public void setRecipient(OutgoingRecipient recipient) {
        this.recipient = recipient;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) throws InvalidNotificationTypeException {
        switch (notificationType) {
            case NOTIFICATION_TYPE_REGULAR:
                this.notificationType = NOTIFICATION_TYPE_REGULAR;
                break;
            case NOTIFICATION_TYPE_SILENT:
                this.notificationType = NOTIFICATION_TYPE_SILENT;
                break;
            case NOTIFICATION_TYPE_NO_PUSH:
                this.notificationType = NOTIFICATION_TYPE_NO_PUSH;
                break;
            default:
                throw new InvalidNotificationTypeException();
        }
    }

    /**
     * Check whether the type of the message passed as an argument matches with one of the provided types. In case a
     * match is not found, an UnsupportedOperationException will be thrown.
     *
     * @param messageToCheck The message to check for its type.
     * @param supportedTypes A list of supported types.
     */
    public void checkMessageTypeCompatibility(OutgoingMessage messageToCheck, OutgoingMessageType... supportedTypes) {
        // Extract the type of the message passed as an argument
        OutgoingMessageType messageType = messageToCheck.getOutgoingMessageType();

        // Check if it matches with one of the supported types
        for (OutgoingMessageType supportedType : supportedTypes) {
            if (messageType == supportedType) {
                return;
            }
        }

        // Throw an exception if a match is not found
        throw new UnsupportedOperationException(Constants.MSG_OPERATION_NOT_SUPPORTED_BY_MESSAGE_TYPE);
    }

    public abstract OutgoingMessageType getOutgoingMessageType();

    public static class OutgoingRecipient {
        private String phoneNumber;
        private String id;

        public OutgoingRecipient() {
        }

        public OutgoingRecipient(String phoneNumber, String id) {
            if (phoneNumber == null ^ id == null) {
                this.phoneNumber = phoneNumber;
                this.id = id;
            } else {
                throw new InvalidRecipientException();
            }
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getId() {
            return id;
        }
    }

    public static class Builder {
        private OutgoingMessageType messageType;

        public Builder(OutgoingMessageType messageType) {
            this.messageType = messageType;
        }

        public OutgoingMessage build() {
            // TODO: Add implementation
            return null;
        }
    }

    /**
     * Class representing a quick reply. An array of quick replies can be added to every text, multimedia and
     * template message.
     */
    public static class QuickReply {
        private String contentType = QUICK_REPLY_CONTENT_TYPE;
        private String title;
        private String payload;

        public QuickReply(String title, String payload) {
            this.title = title;
            this.payload = payload;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

    public enum OutgoingMessageType {
        SENDER_ACTION,
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO,
        FILE,
        TEMPLATE_GENERIC,
        TEMPLATE_BUTTON
    }
}

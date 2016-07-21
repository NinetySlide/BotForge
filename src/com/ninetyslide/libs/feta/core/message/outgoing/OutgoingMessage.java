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
import com.ninetyslide.libs.feta.core.message.outgoing.feature.ValidityChecker;
import com.ninetyslide.libs.feta.exception.InvalidNotificationTypeException;
import com.ninetyslide.libs.feta.exception.InvalidRecipientException;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

/**
 * Abstract class for all the outgoing messages.
 */
public abstract class OutgoingMessage implements ValidityChecker {

    public final static String NOTIFICATION_TYPE_REGULAR = "REGULAR";
    public final static String NOTIFICATION_TYPE_SILENT = "SILENT_PUSH";
    public final static String NOTIFICATION_TYPE_NO_PUSH = "NO_PUSH";

    public final static String QUICK_REPLY_CONTENT_TYPE = "text";

    private OutgoingRecipient recipient;
    private String notificationType = null;

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
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        return recipient != null;
    }

    /**
     * Check whether the type of the message passed as an argument matches with one of the provided types. In case a
     * match is not found, an UnsupportedOperationException will be thrown.
     *
     * @param messageToCheck The message to check for its type.
     * @param supportedTypes A list of supported types.
     */
    public static void checkMessageTypeCompatibility(OutgoingMessage messageToCheck, OutgoingMessageType... supportedTypes) {
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
    }

    public static class Builder {
        private OutgoingMessageType messageType = null;
        OutgoingMessage message = null;

        public Builder(OutgoingMessageType messageType) {
            this.messageType = messageType;
            // TODO: Add Complete implementation
        }

        public OutgoingMessage build() {
            // TODO: Add implementation
            return null;
        }

        /**
         * Set a Sender Action for this message.
         *
         * @param senderAction The Sender Action to set for this message.
         * @return The builder instance used to invoke this method.
         */
        public Builder setSenderAction(SenderActionsMessage.SenderAction senderAction) {
            // This method is only available for SenderActionMessage
            checkMessageTypeCompatibility(message, OutgoingMessageType.SENDER_ACTION);

            // Set the Sender Action
            ((SenderActionsMessage) message).setSenderAction(senderAction);

            return this;
        }

        /**
         * Set the text for the outgoing message or for the button template. Same as setText(text, false).
         *
         * @param text The text that will be assigned to the message or to the button template.
         * @throws TextLengthExceededException When the character limit is exceeded.
         * @return The builder instance used to invoke this method.
         */
        public Builder setText(String text) throws TextLengthExceededException {
            return setText(text, false);
        }

        /**
         * Set the text for the outgoing message or for the button template. Please note that at the time of this
         * version, the length of the text is limited to 320 characters. If you exceed this limit, an exception will
         * be thrown. However, if you know what you are doing, you can set the parameter force to true so that the
         * limit will not be enforced.
         *
         * @param text The text that will be assigned to the message or to the button template.
         * @param force Whether the character limit must be enforced.
         * @return The builder instance used to invoke this method.
         * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to
         * false.
         */
        public Builder setText(String text, boolean force) throws TextLengthExceededException {
            // This method is only available for OutgoingTextMessage and Button Template Message
            checkMessageTypeCompatibility(message, OutgoingMessageType.TEXT, OutgoingMessageType.TEMPLATE_BUTTON);

            switch(this.messageType) {
                case TEXT:
                    // Set the text for OutgoingTextMessage
                    ((OutgoingTextMessage) message).setText(text, force);
                    break;
                case TEMPLATE_BUTTON:
                    // Set the text for Button Template
                    ((OutgoingTemplateMessage) message).setText(text, force);
                    break;
            }

            return this;
        }

        /**
         * Set the URL for the media attached to this message.
         *
         * @param url The URL to attach to the message.
         * @return The builder instance used to invoke this method.
         */
        public Builder setMediaUrl(String url) {
            // This method is only available for OutgoingMultimediaMessage
            checkMessageTypeCompatibility(message, OutgoingMessageType.AUDIO, OutgoingMessageType.FILE, OutgoingMessageType.IMAGE, OutgoingMessageType.VIDEO);

            // Set the URL for OutgoingMultimediaMessage
            ((OutgoingMultimediaMessage) message).setMediaUrl(url);

            return this;
        }

        // TODO: Implement addButton method, remove overloaded method with default parameter from OutgoingTemplateMessage class, implement it here and make the original method package-private
        // TODO: Implement addbubble method, remove overloaded method with default parameter from OutgoingTemplateMessage class, implement it here and make the original method package-private
        // TODO: Implement addQuickReply method and remove overloaded method with default parameter from abstract class
        // TODO: Check for null collection objects in for loops.

    }

    /**
     * Class representing a quick reply. An array of quick replies can be added to every text, multimedia and
     * template message.
     */
    public static class QuickReply implements ValidityChecker {
        private String contentType = QUICK_REPLY_CONTENT_TYPE;
        private String title = null;
        private String payload = null;

        public QuickReply(String title, String payload) {
            this.title = title;
            this.payload = payload;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPayload(String payload) {
            this.payload = payload;
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

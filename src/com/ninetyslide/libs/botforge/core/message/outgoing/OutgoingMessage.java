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

package com.ninetyslide.libs.botforge.core.message.outgoing;

import com.ninetyslide.libs.botforge.common.Constants;
import com.ninetyslide.libs.botforge.core.message.outgoing.feature.QuickRepliesSetter;
import com.ninetyslide.libs.botforge.core.message.outgoing.feature.ValidityChecker;
import com.ninetyslide.libs.botforge.core.message.outgoing.widget.Bubble;
import com.ninetyslide.libs.botforge.core.message.outgoing.widget.Button;
import com.ninetyslide.libs.botforge.exception.ElementsNumberExceededException;
import com.ninetyslide.libs.botforge.exception.TextLengthExceededException;

/**
 * Abstract class that is superclass for all the outgoing messages.
 */
public abstract class OutgoingMessage implements ValidityChecker {

    private final static String NOTIFICATION_TYPE_REGULAR = "REGULAR";
    private final static String NOTIFICATION_TYPE_SILENT = "SILENT_PUSH";
    private final static String NOTIFICATION_TYPE_NO_PUSH = "NO_PUSH";

    private OutgoingRecipient recipient = null;
    private String notificationType = null;

    OutgoingMessage() {
        setNotificationType(NotificationType.REGULAR);
    }

    /**
     * Set the notification type for this message. Notifications can be: regular, silent and no push.
     *
     * @param type The notification type.
     */
    private void setNotificationType(NotificationType type) {
        switch (type) {
            case REGULAR:
                this.notificationType = NOTIFICATION_TYPE_REGULAR;
                break;
            case SILENT:
                this.notificationType = NOTIFICATION_TYPE_SILENT;
                break;
            case NO_PUSH:
                this.notificationType = NOTIFICATION_TYPE_NO_PUSH;
                break;
            default:
                throw new IllegalArgumentException(Constants.MSG_NOTIFICATION_TYPE_INVALID);
        }
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        // In future this may change and verify some actual statements
        return true;
    }

    /**
     * Set the recipient for this message.
     *
     * @param recipient The recipient for this message.
     */
    public void setRecipient(OutgoingRecipient recipient) {
        this.recipient = recipient;
    }

    public abstract OutgoingMessageType getOutgoingMessageType();

    public final static class OutgoingRecipient {
        private String phoneNumber = null;
        private String id = null;

        private OutgoingRecipient() {
        }

        public OutgoingRecipient(String phoneNumber, String id) {
            if (phoneNumber == null ^ id == null) {
                this.phoneNumber = phoneNumber;
                this.id = id;
            } else {
                throw new IllegalArgumentException(Constants.MSG_RECIPIENT_INVALID);
            }
        }
    }

    /**
     * This class is used to build a legal instance of a message. It is the only way to create a message. Create a new
     * instance of the Builder passing the desired Message Type, then use all the exposed methods to compose your
     * message and finally call build() to get the message.
     */
    public final static class Builder {
        // TODO: Handle the sending of location message (if possible)

        private OutgoingMessageType messageType = null;
        OutgoingMessage message = null;

        /**
         * This constructor creates a new builder for the type of message passed as an argument.
         *
         * @param messageType The type of the desired message.
         */
        public Builder(OutgoingMessageType messageType) {
            this.messageType = messageType;

            // Create the right kind of message based on the message type passed as argument
            switch (messageType) {
                case SENDER_ACTION:
                    message = new SenderActionsMessage();
                    break;
                case TEXT:
                    message = new OutgoingTextMessage();
                    break;
                case IMAGE:
                    message = new OutgoingMultimediaMessage(OutgoingMessageType.IMAGE);
                    break;
                case AUDIO:
                    message = new OutgoingMultimediaMessage(OutgoingMessageType.AUDIO);
                    break;
                case VIDEO:
                    message = new OutgoingMultimediaMessage(OutgoingMessageType.VIDEO);
                    break;
                case FILE:
                    message = new OutgoingMultimediaMessage(OutgoingMessageType.FILE);
                    break;
                case TEMPLATE_GENERIC:
                    message = new OutgoingTemplateMessage(OutgoingMessageType.TEMPLATE_GENERIC);
                    break;
                case TEMPLATE_BUTTON:
                    message = new OutgoingTemplateMessage(OutgoingMessageType.TEMPLATE_BUTTON);
                    break;
                default:
                    throw new IllegalArgumentException(Constants.MSG_MESSAGE_TYPE_INVALID);
            }
        }

        /**
         * This method returns the message that has been build so far. If the mesage is still incomplete or is invalid,
         * an exception will be thrown.
         *
         * @return The message object resulting from  building.
         */
        public OutgoingMessage build() {
            if (message.isValid()) {
                return message;
            } else {
                throw new IllegalArgumentException(Constants.MSG_MESSAGE_INVALID);
            }
        }

        /**
         * Set a Sender Action for this message. This method is only available for SenderActionMessage.
         *
         * @param senderAction The Sender Action to set for this message.
         * @return The builder instance used to invoke this method.
         */
        public Builder setSenderAction(SenderActionsMessage.SenderAction senderAction) {
            // This method is only available for SenderActionMessage
            checkMessageTypeCompatibility(OutgoingMessageType.SENDER_ACTION);

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
         * Set the text for the outgoing message or for the button template. This method is only available for
         * OutgoingTextMessage and Button Template Message. Please note that at the time of this version, the length
         * of the text is limited to 320 characters. If you exceed this limit, an exception will be thrown. However,
         * if you know what you are doing, you can set the parameter force to true so that the limit will not be
         * enforced.
         *
         * @param text The text that will be assigned to the message or to the button template.
         * @param force Whether the character limit must be enforced.
         * @return The builder instance used to invoke this method.
         * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to
         * false.
         */
        public Builder setText(String text, boolean force) throws TextLengthExceededException {
            // This method is only available for OutgoingTextMessage and Button Template Message
            checkMessageTypeCompatibility(OutgoingMessageType.TEXT, OutgoingMessageType.TEMPLATE_BUTTON);

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
         * Set the URL for the media attached to this message. This method is only available for
         * OutgoingMultimediaMessage.
         *
         * @param url The URL to attach to the message.
         * @return The builder instance used to invoke this method.
         */
        public Builder setMediaUrl(String url) {
            // This method is only available for OutgoingMultimediaMessage
            checkMessageTypeCompatibility(
                    OutgoingMessageType.AUDIO,
                    OutgoingMessageType.FILE,
                    OutgoingMessageType.IMAGE,
                    OutgoingMessageType.VIDEO
            );

            // Set the URL for OutgoingMultimediaMessage
            ((OutgoingMultimediaMessage) message).setMediaUrl(url);

            return this;
        }

        /**
         * Add a new button to the template. Same as addButton(button, false).
         *
         * @param button The Button to add to the template.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the buttons limit is exceeded.
         */
        public Builder addButton(Button button) throws ElementsNumberExceededException {
            return addButton(button, false);
        }

        /**
         * Add a new button to the template. This method is only available for Button Template message. Please note
         * that at the time of this version, the number of buttons is limited to 3. If you exceed this limit, an
         * exception will be thrown. However, if you know what you are doing, you can set the parameter force to true
         * so that the limit will not be enforced.
         *
         * @param button The Button to add to the template.
         * @param force Whether the buttons limit must be enforced.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the buttons limit is exceeded and the force parameter is set to
         * false.
         */
        public Builder addButton(Button button, boolean force) throws ElementsNumberExceededException {
            // This method is only available for Button Template message
            checkMessageTypeCompatibility(OutgoingMessageType.TEMPLATE_BUTTON);

            // Add a Button to the template
            ((OutgoingTemplateMessage) message).addButton(button, force);

            return this;
        }

        /**
         * Add a new bubble to the template. Same as addBubble(bubble, false).
         *
         * @param bubble The Bubble to add to the template.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the bubbles limit is exceeded.
         */
        public Builder addBubble(Bubble bubble) throws ElementsNumberExceededException {
            return addBubble(bubble, false);
        }

        /**
         * Add a new bubble to the template. This method is only available for Generic Template Message. Please note
         * that at the time of this version, the number of bubbles is limited to 10. If you exceed this limit, an
         * exception will be thrown. However, if you know what you are doing, you can set the parameter force to true
         * so that the limit will not be enforced.
         *
         * @param bubble The Bubble to add to the template.
         * @param force Whether the bubbles limit must be enforced.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the bubbles limit is exceeded and the force arameter is set to
         * false.
         */
        public Builder addBubble(Bubble bubble, boolean force) throws ElementsNumberExceededException {
            // This method is only available for Generic Template Message
            checkMessageTypeCompatibility(OutgoingMessageType.TEMPLATE_GENERIC);

            // Add a Bubble to the template
            ((OutgoingTemplateMessage) message).addBubble(bubble, force);

            return this;
        }

        /**
         * Set the notification type for this message. Notifications can be: regular, silent and no push.
         *
         * @param type The notification type.
         * @return The builder instance used to invoke this method.
         */
        public Builder setNotificationType(NotificationType type) {
            // Set the notification type
            message.setNotificationType(type);

            return this;
        }

        /**
         * Add a QuickReply to the message. Same as addQuickReply(quickReply, false).
         *
         * @param quickReply The QuickReply to add to the message.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the quick replies limit is exceeded.
         */
        public Builder addQuickReply(QuickReply quickReply) throws ElementsNumberExceededException {
            return addQuickReply(quickReply, false);
        }

        /**
         * Add a QuickReply to the message. This method is only available for OutgoingTextMessage,
         * OutgoingMultimediaMessage and OutgoingTemplateMessage. Please note that at the time of this version, the
         * number of quick replies is limited to 10. If you exceed this limit, an exception will be thrown. However,
         * if you know what you are doing, you can set the parameter force to true so that the limit will not be
         * enforced.
         *
         * @param quickReply The QuickReply to add to the message.
         * @param force Whether the quick replies limit must be enforced.
         * @return The builder instance used to invoke this method.
         * @throws ElementsNumberExceededException When the quick replies limit is exceeded and the force parameter
         * is set to false.
         */
        public Builder addQuickReply(QuickReply quickReply, boolean force) throws ElementsNumberExceededException {
            // This method is only available for OutgoingTextMessage, OutgoingMultimediaMessage and OutgoingTemplateMessage
            checkMessageTypeCompatibility(
                    OutgoingMessageType.TEXT,
                    OutgoingMessageType.IMAGE,
                    OutgoingMessageType.AUDIO,
                    OutgoingMessageType.VIDEO,
                    OutgoingMessageType.FILE,
                    OutgoingMessageType.TEMPLATE_BUTTON,
                    OutgoingMessageType.TEMPLATE_GENERIC
            );

            // Add the quickReply
            ((QuickRepliesSetter) message).addQuickReply(quickReply, force);

            return this;
        }

        /**
         * Check whether the type of the message being build matches with one of the provided types. In case a match
         * is not found, an UnsupportedOperationException will be thrown.
         *
         * @param supportedTypes A list of supported types.
         */
        private void checkMessageTypeCompatibility(OutgoingMessageType... supportedTypes) {
            // Check if it matches with one of the supported types
            if (supportedTypes != null) {
                for (OutgoingMessageType supportedType : supportedTypes) {
                    if (messageType == supportedType) {
                        return;
                    }
                }
            }

            // Throw an exception if a match is not found
            throw new UnsupportedOperationException(Constants.MSG_MESSAGE_OPERATION_INVALID);
        }

    }

    /**
     * Class representing a quick reply. An array of quick replies can be added to every text, multimedia and
     * template message.
     */
    public final static class QuickReply implements ValidityChecker {
        private final static String QUICK_REPLY_CONTENT_TYPE = "text";

        private String contentType = QUICK_REPLY_CONTENT_TYPE;
        private String title = null;
        private String payload = null;

        private QuickReply() {
        }

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

    public enum NotificationType {
        REGULAR,
        SILENT,
        NO_PUSH
    }
}

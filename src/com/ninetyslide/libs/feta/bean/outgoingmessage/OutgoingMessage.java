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

package com.ninetyslide.libs.feta.bean.outgoingmessage;

import com.ninetyslide.libs.feta.exception.InvalidNotificationTypeException;

/**
 * Abstract class for all the outgoing messages.
 */
public abstract class OutgoingMessage {

    public final static String NOTIFICATION_TYPE_REGULAR = "REGULAR";
    public final static String NOTIFICATION_TYPE_SILENT = "SILENT_PUSH";
    public final static String NOTIFICATION_TYPE_NO_PUSH = "NO_PUSH";

    private OutgoingRecipient recipient;
    private String notificationType;

    public OutgoingMessage() {
    }

    public OutgoingMessage(OutgoingRecipient recipient, String notificationType) throws InvalidNotificationTypeException {
        this.recipient = recipient;
        setNotificationType(notificationType);
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

    public static class OutgoingRecipient {
        private String phoneNumber;
        private String id;

        public OutgoingRecipient() {
        }

        public OutgoingRecipient(String phoneNumber, String id) {
            this.phoneNumber = phoneNumber;
            this.id = id;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getId() {
            return id;
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

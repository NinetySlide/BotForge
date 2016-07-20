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

/**
 * Class representing a Sender Action message.
 */
public class SenderActionsMessage extends OutgoingMessage {

    private final static String ACTION_MARK_SEEN = "mark_seen";
    private final static String ACTION_TYPING_ON = "typing_on";
    private final static String ACTION_TYPING_OFF = "typing_off";

    private String senderAction;

    @Override
    public OutgoingMessageType getOutgoingMessageType() {
        return OutgoingMessageType.SENDER_ACTION;
    }

    public SenderAction getSenderAction() {
        switch (senderAction) {
            case ACTION_MARK_SEEN:
                return SenderAction.MARK_SEEN;
            case ACTION_TYPING_ON:
                return SenderAction.TYPING_ON;
            case ACTION_TYPING_OFF:
                return SenderAction.TYPING_OFF;
            default:
                return null;
        }
    }

    public void setSenderAction(SenderAction senderAction) {
        switch (senderAction) {
            case MARK_SEEN:
                this.senderAction = ACTION_MARK_SEEN;
                break;
            case TYPING_ON:
                this.senderAction = ACTION_TYPING_ON;
                break;
            case TYPING_OFF:
                this.senderAction = ACTION_TYPING_OFF;
                break;
        }
    }

    public enum SenderAction {
        MARK_SEEN,
        TYPING_ON,
        TYPING_OFF
    }
}

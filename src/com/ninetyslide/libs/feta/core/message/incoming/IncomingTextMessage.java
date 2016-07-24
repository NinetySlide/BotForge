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

package com.ninetyslide.libs.feta.core.message.incoming;

/**
 * Class representing a Received Text Message.
 */
public final class IncomingTextMessage extends ReceivedMessage {

    private String text = null;
    private QuickReply quickReply = null;

    private IncomingTextMessage() {
    }

    /**
     * Return the type of the message, in this case it is text message.
     *
     * @return The type of the message.
     */
    @Override
    public IncomingMessageType getIncomingMessageType() {
        return IncomingMessageType.TEXT;
    }

    /**
     * Return the text contained inside the received message.
     *
     * @return The text of the message.
     */
    public String getText() {
        return text;
    }

    /**
     * Return the quick reply payload associated with the received text message.
     *
     * @return The quick reply for the message.
     */
    public String getQuickReply() {
        return quickReply.payload;
    }

    private final static class QuickReply {
        String payload = null;
    }
}

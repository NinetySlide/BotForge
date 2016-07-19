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

package com.ninetyslide.libs.feta.bean.incomingmessage;

/**
 * Class representing a Received Text Message.
 */
public class IncomingTextMessage extends ReceivedMessage {

    private String text;
    private QuickReply quickReply;

    public IncomingTextMessage() {
    }

    @Override
    public IncomingMessageType getIncomingMessageType() {
        return IncomingMessageType.TEXT;
    }

    public String getText() {
        return text;
    }

    public String getQuickReply() {
        return quickReply.payload;
    }

    private static class QuickReply {
        String payload;
    }
}

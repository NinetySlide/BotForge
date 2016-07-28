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

package com.ninetyslide.libs.botforge.core.message.incoming;

/**
 * Superclass for IncomingTextMessage and IncomingAttachmentMessage.
 */
public abstract class ReceivedMessage extends IncomingMessage {

    private String mid = null;
    private int seq;
    private boolean isEcho = false;
    private String appId = null;
    private String metadata = null;
    private long stickerId;

    // TODO: Handle the reception of Link sharing message
    ReceivedMessage() {
    }

    public String getMid() {
        return mid;
    }

    public int getSeq() {
        return seq;
    }

    public boolean isEcho() {
        return isEcho;
    }

    public String getAppId() {
        return appId;
    }

    public String getMetadata() {
        return metadata;
    }

    public long getStickerId() {
        return stickerId;
    }

    public abstract IncomingMessageType getIncomingMessageType();

    public enum IncomingMessageType {
        TEXT,
        ATTACHMENT
    }

}

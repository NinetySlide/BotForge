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
 * Superclass for TextMessage and AttachmentMessage.
 */
public abstract class ReceivedMessage extends IncomingMessage {

    private String mid;
    private int seq;
    private boolean isEcho = false;
    private String appId;
    private String metadata;

    public ReceivedMessage() {
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

    public abstract MessageType getMessageType();

    public enum MessageType {
        TEXT,
        ATTACHMENT
    }

}

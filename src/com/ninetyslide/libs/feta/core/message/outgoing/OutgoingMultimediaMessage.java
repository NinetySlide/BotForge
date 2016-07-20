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

import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesCarrier;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesSetter;
import com.ninetyslide.libs.feta.exception.QuickRepliesNumberExceededException;

/**
 * Class representing an outgoing message with multimedia attachments.
 */
public class OutgoingMultimediaMessage extends OutgoingMessage implements QuickRepliesSetter {

    private final static String TYPE_AUDIO = "audio";
    private final static String TYPE_IMAGE = "image";
    private final static String TYPE_VIDEO = "video";
    private final static String TYPE_FILE = "file";

    private transient OutgoingMessageType messageType;
    private MediaRoot message;

    public OutgoingMultimediaMessage(OutgoingMessageType messageType) {
        super();

        // Set the message type
        this.messageType = messageType;

        // Create the class internal structure
        message = new MediaRoot();
        message.attachment = new MediaAttachment();
        message.attachment.payload = new MediaPayload();

        // Assign the String type
        switch (messageType) {
            case AUDIO:
                message.attachment.type = TYPE_AUDIO;
                break;
            case IMAGE:
                message.attachment.type = TYPE_IMAGE;
                break;
            case VIDEO:
                message.attachment.type = TYPE_VIDEO;
                break;
            case FILE:
                message.attachment.type = TYPE_FILE;
                break;
        }
    }

    @Override
    public OutgoingMessageType getOutgoingMessageType() {
        return messageType;
    }

    @Override
    public void addQuickReply(QuickReply quickReply) throws QuickRepliesNumberExceededException {
        addQuickReply(quickReply, false);
    }

    @Override
    public void addQuickReply(QuickReply quickReply, boolean force) throws QuickRepliesNumberExceededException {
        message.addQuickReply(quickReply, force);
    }

    /**
     * Set the URL for the media attached to this message.
     *
     * @param url The URL to attach to the message.
     */
    public void setUrl(String url) {
        message.attachment.payload.url = url;
    }

    private static class MediaRoot extends QuickRepliesCarrier {
        MediaAttachment attachment;
    }

    private static class MediaAttachment {
        String type;
        MediaPayload payload;
    }

    private static class MediaPayload {
        String url;
    }
}

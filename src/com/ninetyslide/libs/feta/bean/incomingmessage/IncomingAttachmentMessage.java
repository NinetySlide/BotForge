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

import com.google.gson.annotations.SerializedName;

/**
 * Class representing a Received Message with Attachments.
 */
public class IncomingAttachmentMessage extends ReceivedMessage {

    private final static String TYPE_IMAGE = "image";
    private final static String TYPE_AUDIO = "audio";
    private final static String TYPE_VIDEO = "video";
    private final static String TYPE_FILE = "file";
    private final static String TYPE_LOCATION = "location";

    private IncomingAttachment[] attachments;

    public IncomingAttachmentMessage() {
    }

    @Override
    public IncomingMessageType getIncomingMessageType() {
        return IncomingMessageType.ATTACHMENT;
    }

    public IncomingAttachment[] getAttachments() {
        return attachments;
    }

    public IncomingAttachment getAttachment(int pos) {
        return attachments[pos];
    }

    public int getAttachmentsNumber() {
        return attachments.length;
    }

    /**
     * Class representing the attachment of the message. It has a type and a payload that might contain different
     * values based on its type.
     */
    private static class IncomingAttachment {
        private String type;
        private IncomingAttachmentPayload payload;

        public IncomingAttachment() {
        }

        public IncomingAttachmentType getAttachmentType() {
            switch (type) {
                case TYPE_IMAGE:
                    return IncomingAttachmentType.IMAGE;
                case TYPE_AUDIO:
                    return IncomingAttachmentType.AUDIO;
                case TYPE_VIDEO:
                    return IncomingAttachmentType.VIDEO;
                case TYPE_FILE:
                    return IncomingAttachmentType.FILE;
                case TYPE_LOCATION:
                    return IncomingAttachmentType.LOCATION;
                default:
                    return IncomingAttachmentType.UNKNOWN;
            }
        }

        public IncomingAttachmentPayload getPayload() {
            return payload;
        }
    }

    /**
     * Class representing an attachment payload. Based on the type of the attachment, either url field or coordinates
     * field is set, not both.
     */
    private static class IncomingAttachmentPayload {
        private String url;
        private Coordinates coordinates;

        public IncomingAttachmentPayload() {
        }

        public String getUrl() {
            return url;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }
    }

    /**
     * Class representing a couple of coordinates, latitude and longitude.
     */
    private static class Coordinates {
        private double lat;
        @SerializedName("long") private double longitude;

        public Coordinates() {
        }

        public double getLat() {
            return lat;
        }

        public double getLong() {
            return longitude;
        }
    }

    /**
     * Enum used to describe the attachment type.
     */
    public enum IncomingAttachmentType {
        IMAGE,
        AUDIO,
        VIDEO,
        FILE,
        LOCATION,
        UNKNOWN
    }
}

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

import com.google.gson.annotations.SerializedName;

/**
 * Class representing a Received Message with Attachments.
 */
public final class IncomingAttachmentMessage extends ReceivedMessage {

    private final static String TYPE_IMAGE = "image";
    private final static String TYPE_AUDIO = "audio";
    private final static String TYPE_VIDEO = "video";
    private final static String TYPE_FILE = "file";
    private final static String TYPE_LOCATION = "location";

    private IncomingAttachment[] attachments = null;

    private IncomingAttachmentMessage() {
    }

    /**
     * Return the type of the message, in this case it is an attachment message.
     *
     * @return The type of the message.
     */
    @Override
    public IncomingMessageType getIncomingMessageType() {
        return IncomingMessageType.ATTACHMENT;
    }

    /**
     * Retrieve the attachments of this message.
     *
     * @return An array of attachments related to this message.
     */
    public IncomingAttachment[] getAttachments() {
        return attachments;
    }

    /**
     * Retrieve the attachment specified by the index passed as an argument.
     *
     * @param pos The index of the attachment.
     * @return The desired attachment of this message.
     */
    public IncomingAttachment getAttachment(int pos) {
        return attachments[pos];
    }

    /**
     * Get the number of the attachments for this message.
     *
     * @return The number of attachments for this message.
     */
    public int getAttachmentsNumber() {
        return attachments.length;
    }

    /**
     * Class representing the attachment of the message. It has a type and a payload that might contain different
     * values based on its type.
     */
    public final static class IncomingAttachment {
        private String type = null;
        private IncomingAttachmentPayload payload = null;

        private IncomingAttachment() {
        }

        /**
         * Return the type of the resource contained inside the attachment.
         *
         * @return The type of the resource.
         */
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

        /**
         * Return the URL of the attached resource.
         *
         * @return The URL of the attached resource.
         */
        public String getUrl() {
            return payload.getUrl();
        }

        /**
         * Return the coordinates contained inside the attachment.
         *
         * @return The coordinates contained in the attachment.
         */
        public Coordinates getCoordinates() {
            return payload.getCoordinates();
        }
    }

    /**
     * Class representing an attachment payload. Based on the type of the attachment, either url field or coordinates
     * field is set, not both.
     */
    private final static class IncomingAttachmentPayload {
        private String url = null;
        private Coordinates coordinates = null;

        private IncomingAttachmentPayload() {
        }

        String getUrl() {
            return url;
        }

        Coordinates getCoordinates() {
            return coordinates;
        }
    }

    /**
     * Class representing a couple of coordinates, latitude and longitude.
     */
    public final static class Coordinates {
        private double lat;
        @SerializedName("long") private double longitude;

        private Coordinates() {
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

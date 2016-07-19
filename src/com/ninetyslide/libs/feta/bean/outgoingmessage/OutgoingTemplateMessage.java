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

import com.ninetyslide.libs.feta.bean.outgoingmessage.feature.QuickRepliesCarrier;
import com.ninetyslide.libs.feta.bean.outgoingmessage.feature.QuickRepliesSetter;
import com.ninetyslide.libs.feta.bean.outgoingmessage.widget.Bubble;
import com.ninetyslide.libs.feta.bean.outgoingmessage.widget.Button;
import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.exception.QuickRepliesNumberExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Template outgoing message. The supported templates are "generic" and "button".
 */
public class OutgoingTemplateMessage extends OutgoingMessage implements QuickRepliesSetter {

    private final static String TYPE_GENERIC = "generic";
    private final static String TYPE_BUTTON = "button";

    private transient OutgoingMessageType messageType;
    private TemplateMessage message;

    public OutgoingTemplateMessage(OutgoingMessageType messageType) {
        super();

        switch (messageType) {
            case TEMPLATE_GENERIC:
                this.messageType = messageType;
                // TODO: Handle this situation
                break;
            case TEMPLATE_BUTTON:
                this.messageType = messageType;
                // TODO: Handle this situation
                break;
            default:
                throw new IllegalArgumentException(Constants.MSG_INVALID_TEMPLATE_TYPE);
        }
    }

    @Override
    public OutgoingMessageType getOutgoingMessageType() {
        return messageType;
    }

    @Override
    public void addQuickReply(QuickReply quickReply, boolean force) throws QuickRepliesNumberExceededException {
        message.addQuickReply(quickReply, force);
    }

    public void setText(String text) {
        // Only for button template
        // button text is limited to 320 char
        // TODO: Add implementation
    }

    public void addButton(Button button) {
        // Only for button template
        // buttons are limited to 3
        // TODO: Add implementation
    }

    public void addBubble(Bubble bubble) {
        // only for generic template
        // bubble is limited to 10
        // TODO: Add implementation
    }

    private static class TemplateMessage extends QuickRepliesCarrier {
        TemplateAttachment attachment;
    }

    private static class TemplateAttachment {
        String type;
        Template payload;
    }

    /**
     * Superclass for the templates.
     */
    private abstract static class Template {
        String templateType;

    }

    /**
     * Class representing the Generic template, which contains an array of Bubbles.
     */
    private static class GenericTemplate extends Template {
        List<Bubble> elements = new ArrayList<>();

        public GenericTemplate() {
            templateType = TYPE_GENERIC;
        }
    }

    /**
     * Class representing the Button template, which contains an array of Buttons.
     */
    private static class ButtonTemplate extends Template {
        String text;
        List<Button> buttons = new ArrayList<>();

        public ButtonTemplate() {
            templateType = TYPE_BUTTON;
        }
    }
}

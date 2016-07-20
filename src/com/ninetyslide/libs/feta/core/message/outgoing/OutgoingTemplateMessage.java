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
import com.ninetyslide.libs.feta.core.message.outgoing.widget.Bubble;
import com.ninetyslide.libs.feta.core.message.outgoing.widget.Button;
import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.exception.BubblesNumberExceededException;
import com.ninetyslide.libs.feta.exception.ButtonsNumberExceededException;
import com.ninetyslide.libs.feta.exception.QuickRepliesNumberExceededException;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Template outgoing message. The supported templates are "generic" and "button".
 */
public class OutgoingTemplateMessage extends OutgoingMessage implements QuickRepliesSetter {

    private final static String SUPERTYPE_TEMPLATE = "template";
    private final static String TYPE_GENERIC = "generic";
    private final static String TYPE_BUTTON = "button";

    private transient OutgoingMessageType messageType;
    private TemplateRoot message;

    public OutgoingTemplateMessage(OutgoingMessageType messageType) {
        super();

        // Initialize the template message
        this.messageType = messageType;
        this.message = new TemplateRoot();
        this.message.attachment = new TemplateAttachment();

        // Complete initialization based on the template type
        switch (this.messageType) {
            case TEMPLATE_GENERIC:
                this.message.attachment.payload = new GenericTemplate();
                break;
            case TEMPLATE_BUTTON:
                this.message.attachment.payload = new ButtonTemplate();
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
    public void addQuickReply(QuickReply quickReply) throws QuickRepliesNumberExceededException {
        addQuickReply(quickReply, false);
    }

    @Override
    public void addQuickReply(QuickReply quickReply, boolean force) throws QuickRepliesNumberExceededException {
        message.addQuickReply(quickReply, force);
    }

    /**
     * Set the text for the button template. Same as setText(text, false).
     *
     * @param text The text to set for the template.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public void setText(String text) throws TextLengthExceededException {
        setText(text, false);
    }

    /**
     * Set the text for the button template. Please note that at the time of this version, the length of the text is
     * limited to 320 characters. If you exceed this limit, an exception will be thrown. However, if you know what you
     * are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param text The text to set for the template.
     * @param force Whether the character limit must be enforced.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public void setText(String text, boolean force) throws TextLengthExceededException {
        // Only for Button Template
        checkMessageTypeCompatibility(this, OutgoingMessageType.TEMPLATE_BUTTON);

        // Button text is limited to 320 characters
        if (text != null) {
            if (!force && text.length() > Constants.LIMIT_TEXT_LENGTH) {
                throw new TextLengthExceededException();
            }
            ((ButtonTemplate) this.message.attachment.payload).text = text;
        }
    }

    /**
     * Set a new button for the template. Same as addButton(button, false).
     *
     * @param button The Button to add to the template.
     * @throws ButtonsNumberExceededException When the buttons limit is exceeded.
     */
    public void addButton(Button button) throws ButtonsNumberExceededException {
        addButton(button, false);
    }

    /**
     * Add a new button to the template. Please note that at the time of this version, the number of buttons is limited
     * to 3. If you exceed this limit, an exception will be thrown. However, if you know what you are doing, you can set
     * the parameter force to true so that the limit will not be enforced.
     *
     * @param button The Button to add to the template.
     * @param force Whether the buttons limit must be enforced.
     * @throws ButtonsNumberExceededException When the buttons limit is exceeded and the force parameter is set to false.
     */
    public void addButton(Button button, boolean force) throws ButtonsNumberExceededException {
        // Only for Button Template
        checkMessageTypeCompatibility(this, OutgoingMessageType.TEMPLATE_BUTTON);

        // Add the button to the template
        if (button != null) {
            ButtonTemplate buttonTemplate = (ButtonTemplate) this.message.attachment.payload;

            // Buttons are limited to 3
            if (!force && buttonTemplate.buttons.size() > Constants.LIMIT_BUTTONS) {
                throw new ButtonsNumberExceededException();
            }

            buttonTemplate.buttons.add(button);
        }
    }

    /**
     * Add a new bubble to the template. Same as addBubble(bubble, false).
     *
     * @param bubble The Bubble to add to the template.
     * @throws BubblesNumberExceededException When the bubbles limit is exceeded.
     */
    public void addBubble(Bubble bubble) throws BubblesNumberExceededException {
        addBubble(bubble, false);
    }

    /**
     * Add a new bubble to the template. Please note that at the time of this version, the number of bubbles is limited
     * to 10. If you exceed this limit, an exception will be thrown. However, if you know what you are doing, you can
     * set the parameter force to true so that the limit will not be enforced.
     *
     * @param bubble The Bubble to add to the template.
     * @param force Whether the bubbles limit must be enforced.
     * @throws BubblesNumberExceededException When the bubbles limit is exceeded and the force arameter is set to false.
     */
    public void addBubble(Bubble bubble, boolean force) throws BubblesNumberExceededException {
        // Only for Generic Template
        checkMessageTypeCompatibility(this, OutgoingMessageType.TEMPLATE_GENERIC);

        // Add the bubble to the template
        if (bubble != null) {
            GenericTemplate genericTemplate = (GenericTemplate) this.message.attachment.payload;

            // Bubbles are limited to 10
            if (!force && genericTemplate.elements.size() > Constants.LIMIT_BUBBLES) {
                throw new BubblesNumberExceededException();
            }

            genericTemplate.elements.add(bubble);
        }
    }

    private static class TemplateRoot extends QuickRepliesCarrier {
        TemplateAttachment attachment;
    }

    private static class TemplateAttachment {
        public TemplateAttachment() {
            type = SUPERTYPE_TEMPLATE;
        }

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

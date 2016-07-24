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

import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesCarrier;
import com.ninetyslide.libs.feta.core.message.outgoing.feature.QuickRepliesSetter;
import com.ninetyslide.libs.feta.core.message.outgoing.widget.Bubble;
import com.ninetyslide.libs.feta.core.message.outgoing.widget.Button;
import com.ninetyslide.libs.feta.exception.ElementsNumberExceededException;
import com.ninetyslide.libs.feta.exception.TextLengthExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Template outgoing message. The supported templates are "generic" and "button".
 */
final class OutgoingTemplateMessage extends OutgoingMessage implements QuickRepliesSetter {

    private final static String SUPERTYPE_TEMPLATE = "template";
    private final static String TYPE_GENERIC = "generic";
    private final static String TYPE_BUTTON = "button";

    private transient OutgoingMessageType messageType = null;
    private TemplateRoot message = null;

    private OutgoingTemplateMessage() {
    }

    OutgoingTemplateMessage(OutgoingMessageType messageType) {
        super();

        // Initialize the template message based on the template type
        Template payload = null;

        switch (this.messageType) {
            case TEMPLATE_GENERIC:
                payload = new GenericTemplate();
                break;
            case TEMPLATE_BUTTON:
                payload = new ButtonTemplate();
                break;
            default:
                throw new IllegalArgumentException(Constants.MSG_TEMPLATE_TYPE_INVALID);
        }

        this.messageType = messageType;
        this.message = new TemplateRoot();
        this.message.attachment = new TemplateAttachment();
        this.message.attachment.payload = payload;
    }

    @Override
    public OutgoingMessageType getOutgoingMessageType() {
        return messageType;
    }

    @Override
    public void addQuickReply(QuickReply quickReply, boolean force) throws ElementsNumberExceededException {
        message.addQuickReply(quickReply, force);
    }

    /**
     * Set the text for the button template. This method is only available for Button Template Messages. Please note
     * that at the time of this version, the length of the text is limited to 320 characters. If you exceed this
     * limit, an exception will be thrown. However, if you know what you are doing, you can set the parameter force to
     * true so that the limit will not be enforced.
     *
     * @param text The text to set for the template.
     * @param force Whether the character limit must be enforced.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    void setText(String text, boolean force) throws TextLengthExceededException {

        // Button text is limited to 320 characters
        if (text != null) {
            if (!force && text.length() > Constants.LIMIT_TEXT_LENGTH) {
                throw new TextLengthExceededException(Constants.MSG_TEXT_LENGTH_EXCEEDED);
            }
            ((ButtonTemplate) this.message.attachment.payload).text = text;
        }
    }

    /**
     * Add a new button to the template. This method is only available for Button Template Messages. Please note that
     * at the time of this version, the number of buttons is limited to 3. If you exceed this limit, an exception will
     * be thrown. However, if you know what you are doing, you can set the parameter force to true so that the limit
     * will not be enforced.
     *
     * @param button The Button to add to the template.
     * @param force Whether the buttons limit must be enforced.
     * @throws ElementsNumberExceededException When the buttons limit is exceeded and the force parameter is set to false.
     */
    void addButton(Button button, boolean force) throws ElementsNumberExceededException {

        // Add the button to the template
        if (button != null) {
            ButtonTemplate buttonTemplate = (ButtonTemplate) this.message.attachment.payload;

            // Buttons are limited to 3
            if (!force && buttonTemplate.buttons.size() > Constants.LIMIT_BUTTONS) {
                throw new ElementsNumberExceededException(Constants.MSG_BUTTONS_NUMBER_EXCEEDED);
            }

            buttonTemplate.buttons.add(button);
        }
    }

    /**
     * Add a new bubble to the template. This method is only available for Generic Template Messages. Please note that
     * at the time of this version, the number of bubbles is limited to 10. If you exceed this limit, an exception
     * will be thrown. However, if you know what you are doing, you can set the parameter force to true so that the
     * limit will not be enforced.
     *
     * @param bubble The Bubble to add to the template.
     * @param force Whether the bubbles limit must be enforced.
     * @throws ElementsNumberExceededException When the bubbles limit is exceeded and the force arameter is set to false.
     */
    void addBubble(Bubble bubble, boolean force) throws ElementsNumberExceededException {

        // Add the bubble to the template
        if (bubble != null) {
            GenericTemplate genericTemplate = (GenericTemplate) this.message.attachment.payload;

            // Bubbles are limited to 10
            if (!force && genericTemplate.elements.size() > Constants.LIMIT_BUBBLES) {
                throw new ElementsNumberExceededException(Constants.MSG_BUBBLES_NUMBER_EXCEEDED);
            }

            genericTemplate.elements.add(bubble);
        }
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        // Check that every basic attribute of a template message is valid
        if (message == null ||
                message.attachment == null ||
                message.attachment.type == null ||
                !message.attachment.type.equals(SUPERTYPE_TEMPLATE) ||
                message.attachment.payload == null ||
                message.attachment.payload.templateType == null) {
            return false;
        }

        // Check that every component of a generic template is valid
        if (messageType == OutgoingMessageType.TEMPLATE_GENERIC) {
            GenericTemplate genericTemplate = (GenericTemplate) message.attachment.payload;
            if (!genericTemplate.templateType.equals(TYPE_GENERIC) ||
                    genericTemplate.elements == null ||
                    genericTemplate.elements.size() < 1) {
                return false;
            }
            for (Bubble bubble : genericTemplate.elements) {
                if (!bubble.isValid()) {
                    return false;
                }
            }
        }

        // Check that every component of a button template is valid
        if (messageType == OutgoingMessageType.TEMPLATE_BUTTON) {
            ButtonTemplate buttonTemplate = (ButtonTemplate) message.attachment.payload;
            if (!buttonTemplate.templateType.equals(TYPE_BUTTON) ||
                    buttonTemplate.text == null ||
                    buttonTemplate.buttons == null ||
                    buttonTemplate.buttons.size() < 1) {
                return false;
            }
            for (Button button : buttonTemplate.buttons) {
                if (!button.isValid()) {
                    return false;
                }
            }
        }

        // Check that superclass and message are valid
        return super.isValid() && message.isValid();
    }

    private final static class TemplateRoot extends QuickRepliesCarrier {
        TemplateAttachment attachment = null;

        TemplateRoot() {
        }
    }

    private final static class TemplateAttachment {
        String type = null;
        Template payload = null;

        TemplateAttachment() {
            type = SUPERTYPE_TEMPLATE;
        }
    }

    /**
     * Superclass for the templates.
     */
    private abstract static class Template {
        String templateType = null;

        Template() {
        }
    }

    /**
     * Class representing the Generic template, which contains an array of Bubbles.
     */
    private final static class GenericTemplate extends Template {
        List<Bubble> elements = new ArrayList<>();

        GenericTemplate() {
            templateType = TYPE_GENERIC;
        }
    }

    /**
     * Class representing the Button template, which contains an array of Buttons.
     */
    private final static class ButtonTemplate extends Template {
        String text = null;
        List<Button> buttons = new ArrayList<>();

        ButtonTemplate() {
            templateType = TYPE_BUTTON;
        }
    }
}

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

package com.ninetyslide.libs.botforge.core.message.outgoing.widget;

import com.ninetyslide.libs.botforge.common.Constants;
import com.ninetyslide.libs.botforge.core.message.outgoing.feature.ValidityChecker;
import com.ninetyslide.libs.botforge.exception.ElementsNumberExceededException;
import com.ninetyslide.libs.botforge.exception.TextLengthExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the Bubble widget that can be added to the Generic template.
 */
public final class Bubble implements ValidityChecker {

    private String title = null;
    private String subtitle = null;
    private String imageUrl = null;
    private String itemUrl = null;
    private List<Button> buttons = null;

    public Bubble() {
    }

    /**
     * Set the title for the Bubble. Same as setTitle(title, false).
     *
     * @param title The title to add to the Bubble.
     * @return The instance of the bubble used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public Bubble setTitle(String title) throws TextLengthExceededException {
        return setTitle(title, false);
    }

    /**
     * Set the title for the Bubble. Please note that at the time of this version, the length of the title is limited
     * to 80 characters. If you exceed this limit, an exception will be thrown. However, if you know what you are
     * doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param title The title to add to the Bubble.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the bubble used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    public Bubble setTitle(String title, boolean force) throws TextLengthExceededException {
        if (title != null) {
            // Title has 80 characters limit
            if (!force && title.length() > Constants.LIMIT_BUBBLE_TITLE) {
                throw new TextLengthExceededException(Constants.MSG_TITLE_LENGTH_EXCEEDED);
            }
            this.title = title;
        }

        return this;
    }

    /**
     * Set the subtitle for the Bubble. Same as setSubtitle(subtitle, false).
     *
     * @param subtitle The subtitle to add to the Bubble.
     * @return The instance of the bubble used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded.
     */
    public Bubble setSubtitle(String subtitle) throws TextLengthExceededException {
        return setSubtitle(subtitle, false);
    }

    /**
     * Set the subtitle for the Bubble. Please note that at the time of this version, the length of the subtitle is
     * limited to 80 characters. If you exceed this limit, an exception will be thrown. However, if you know what you
     * are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param subtitle The subtitle to add to the Bubble.
     * @param force Whether the character limit must be enforced.
     * @return The instance of the bubble used to invoke this method.
     * @throws TextLengthExceededException When the character limit is exceeded and the force parameter is set to false.
     */
    public Bubble setSubtitle(String subtitle, boolean force) throws TextLengthExceededException {
        if (subtitle != null) {
            // Subtitle has 80 characters limit
            if (!force && subtitle.length() > Constants.LIMIT_BUBBLE_SUBTITLE) {
                throw new TextLengthExceededException(Constants.MSG_SUBTITLE_LENGTH_EXCEEDED);
            }
            this.subtitle = subtitle;
        }

        return this;
    }

    /**
     * Assign an image URL to the Bubble.
     *
     * @param imageUrl The image URL to assign to the Bubble.
     * @return The instance of the bubble used to invoke this method.
     */
    public Bubble setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    /**
     * Assign an item URL to the Bubble.
     *
     * @param itemUrl The item URL to assign to the Bubble.
     * @return The instance of the bubble used to invoke this method.
     */
    public Bubble setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }

    /**
     * Add a Button to the Bubble. Same as addButton(button, false).
     *
     * @param button The button to add to the bubble.
     * @return The instance of the bubble used to invoke this method.
     * @throws ElementsNumberExceededException When the number of buttons is exceeded.
     */
    public Bubble addButton(Button button) throws ElementsNumberExceededException {
        return addButton(button, false);
    }

    /**
     * Add a Button to the Bubble. Please note that at the time of this version, the number of buttons is limited to 3.
     * If you exceed this limit, an exception will be thrown. However, if you know what you are doing, you can set the
     * parameter force to true so that the limit will not be enforced.
     *
     * @param button The button to add to the bubble.
     * @param force Whether the buttons limit must be enforced.
     * @return The instance of the bubble used to invoke this method.
     * @throws ElementsNumberExceededException When the number of buttons is exceeded and the force parameter is set to
     * false.
     */
    public Bubble addButton(Button button, boolean force) throws ElementsNumberExceededException {
        if (button != null) {
            // Create the List only if needed
            if (this.buttons == null) {
                this.buttons = new ArrayList<>();
            }

            // Buttons are limited to 3
            if (!force && this.buttons.size() > Constants.LIMIT_BUTTONS) {
                throw new ElementsNumberExceededException(Constants.MSG_BUTTONS_NUMBER_EXCEEDED);
            }

            // Add the button
            this.buttons.add(button);
        }

        return this;
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        // Title must be set
        if (title == null) {
            return false;
        }

        // Buttons (if any) must be valid
        if (buttons != null) {
            for (Button button : buttons) {
                if (!button.isValid()) {
                    return false;
                }
            }
        }

        return true;
    }
}

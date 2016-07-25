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

package com.ninetyslide.libs.feta.core.message.outgoing.feature;

import com.ninetyslide.libs.feta.core.message.outgoing.OutgoingMessage;
import com.ninetyslide.libs.feta.common.Constants;
import com.ninetyslide.libs.feta.exception.ElementsNumberExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides the basic infrastructure to add quick replies support to a normal message.
 */
public abstract class QuickRepliesCarrier implements ValidityChecker {
    private List<OutgoingMessage.QuickReply> quickReplies = null;

    /**
     * Add a new quick reply to the list of quick replies. Please note that at the time of this version, the maximum
     * number of quick replies is limited to 10. If you exceed this limit, an exception will be thrown. However, if
     * you know what you are doing, you can set the parameter force to true so that the limit will not be enforced.
     *
     * @param quickReply The quick reply to add to the list.
     */
    public void addQuickReply(OutgoingMessage.QuickReply quickReply, boolean force) throws ElementsNumberExceededException {
        if (quickReply != null) {
            if (quickReplies == null) {
                quickReplies = new ArrayList<>();
            }

            if (!force && quickReplies.size() > Constants.LIMIT_QUICK_REPLIES) {
                throw new ElementsNumberExceededException(Constants.MSG_QUICK_REPLIES_NUMBER_EXCEEDED);
            }

            quickReplies.add(quickReply);
        }
    }

    /**
     * Check whether the message is valid.
     *
     * @return True if the message is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        if (quickReplies != null) {
            for (OutgoingMessage.QuickReply quickReply : quickReplies) {
                if (!quickReply.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }
}

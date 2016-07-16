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

package com.ninetyslide.libs.feta.utils;

import com.ninetyslide.libs.feta.bean.incomingmessage.IncomingMessage;
import com.ninetyslide.libs.feta.bean.outgoingmessage.OutgoingMessage;

/**
 * Class that exposes the facilities to seamlessly convert an incoming message in an outgoing message, so that it's
 * easy to forward received messages to other users.
 */
public class MessageConverter {

    /**
     * This method takes an IncomingMessage and converts it to an OutgoingMessage.
     *
     * @param incomingMessage The received message that must be converted.
     * @return The newly generated OutgoingMessage.
     */
    public static OutgoingMessage getOutgoingMessage(IncomingMessage incomingMessage) {
        // TODO: Add implementation.
        return null;
    }

}

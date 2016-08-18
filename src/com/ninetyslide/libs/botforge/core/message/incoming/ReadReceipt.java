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

package com.ninetyslide.libs.botforge.core.message.incoming;

/**
 * Class representing a Read Receipt.
 */
public final class ReadReceipt extends IncomingMessage {

    private long watermark;
    private int seq;

    private ReadReceipt() {
    }

    public long getWatermark() {
        return watermark;
    }

    public int getSeq() {
        return seq;
    }
}

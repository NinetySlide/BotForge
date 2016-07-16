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

package com.ninetyslide.libs.feta.exception;

import com.ninetyslide.libs.feta.common.Constants;

/**
 * Class for the exception thrown when both phone number and ID are set as the recipient of the mesage or when both
 * are null. This is an unchecked exception.
 */
public class InvalidRecipientException extends RuntimeException {
    public InvalidRecipientException() {
        super(Constants.MSG_RECIPIENT_INVALID);
    }
}

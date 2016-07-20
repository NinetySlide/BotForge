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

package com.ninetyslide.libs.feta.core.message.outgoing.response;

/**
 * Class containing the error information returned by the server when an error occurs while sending a message.
 */
public class SendMessageError {

    public final static int INTERNAL_ERROR = 2;
    public final static int RATE_LIMITED_ERROR = 4;
    public final static int BAD_PARAMETER_ERROR = 100;
    public final static int ACCESS_TOKEN_ERROR = 190;
    public final static int PERMISSION_ERROR = 200;
    public final static int USER_BLOCK_ERROR = 551;
    public final static int ACCOUNT_LINKING_ERROR = 10303;

    private String message;
    private String type;
    private int code;
    private String fbtraceId;

    public SendMessageError() {
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public String getFbtraceId() {
        return fbtraceId;
    }

}

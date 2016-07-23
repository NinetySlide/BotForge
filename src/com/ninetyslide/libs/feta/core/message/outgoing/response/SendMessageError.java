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
public class SendMessageError implements SendMessageResponse {

    public final static int INTERNAL_ERROR = 2;
    public final static int RATE_LIMITED_ERROR = 4;
    public final static int BAD_PARAMETER_ERROR = 100;
    public final static int ACCESS_TOKEN_ERROR = 190;
    public final static int PERMISSION_ERROR = 200;
    public final static int USER_BLOCK_ERROR = 551;
    public final static int ACCOUNT_LINKING_ERROR = 10303;

    // This is not part of the FB specification, it is just there to signal a network error
    public final static int NETWORK_ERROR_CODE = -1;
    public final static String NETWORK_ERROR_TYPE = "Network Error";
    public final static String NETWORK_ERROR_MESSAGE = "An error has occurred during the network request.";
    public final static String NETWORK_ERROR_FBTRACE = "0";

    private String message = null;
    private String type = null;
    private int code;
    private String fbtraceId = null;

    public SendMessageError() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFbtraceId() {
        return fbtraceId;
    }

    public void setFbtraceId(String fbtraceId) {
        this.fbtraceId = fbtraceId;
    }

    @Override
    public boolean hasErrors() {
        return true;
    }
}

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

package com.ninetyslide.libs.feta.core.message.incoming;

/**
 * Class representing an Account Linking Message.
 */
public final class AccountLinking extends IncomingMessage {

    private final static String STATUS_LINKED = "linked";
    private final static String STATUS_UNLINKED = "unlinked";

    private String status = null;
    private String authorizationCode = null;

    private AccountLinking() {
    }

    public Status getStatus() {
        switch (status) {
            case STATUS_LINKED:
                return Status.LINKED;
            case STATUS_UNLINKED:
                return Status.UNLINKED;
            default:
                return Status.UNKNOWN;
        }
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public enum Status {
        LINKED,
        UNLINKED,
        UNKNOWN
    }
}

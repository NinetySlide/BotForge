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

package com.ninetyslide.libs.botforge.adapter;

import com.google.gson.Gson;
import com.ninetyslide.libs.botforge.FbBot;
import com.ninetyslide.libs.botforge.core.BotContext;
import com.ninetyslide.libs.botforge.util.GsonManager;
import com.ninetyslide.libs.botforge.util.NetworkManager;

import java.util.logging.Logger;

/**
 * Class that provides facilities to access User Profile API.
 */
public final class UserProfileApiAdapter {

    private static final Logger log = Logger.getLogger(FbBot.class.getName());

    private final static String USER_PROFILE_API_BASE_URL = "https://graph.facebook.com/v2.6/";
    private final static String USER_PROFILE_REQ_PARAMS = "?fields=first_name,last_name,profile_pic,locale,timezone,gender&access_token=";

    private static Gson gson = GsonManager.getGsonInstance();

    private UserProfileApiAdapter() {
    }

    /**
     * Method used to retrieve the User Profile of a certain user, using the User ID associated with that user.
     *
     * @param context The context of the bot to use for profile retrieval.
     * @param userId The User ID of the desired user.
     * @return The User Profile for the desired user.
     */
    public static UserProfile getUserProfile(BotContext context, String userId) {
        String response = NetworkManager.performGetRequest(
                USER_PROFILE_API_BASE_URL +
                        userId +
                        USER_PROFILE_REQ_PARAMS +
                        context.getPageAccessToken()
        );

        // Log the request data if debug is enabled
        if (context.isDebugEnabled()) {
            log.info("JSON Raw Message: " + response);
        }

        return gson.fromJson(response, UserProfile.class);
    }

    /**
     * Class representing a User Profile.
     */
    public final static class UserProfile {

        private UserProfile() {
        }

        private String firstName = null;
        private String lastName = null;
        private String profilePic = null;
        private String locale = null;
        private double timezone = 0.0;
        private String gender = null;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public String getLocale() {
            return locale;
        }

        public double getTimezone() {
            return timezone;
        }

        public String getGender() {
            return gender;
        }
    }
}

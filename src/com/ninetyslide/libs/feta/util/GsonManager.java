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

package com.ninetyslide.libs.feta.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * Class holding the instances of Gson and JsonParser for the entire project. It's designed as a singleton with two
 * static methods, each one providing lazy initialization of the singleton and the instance they are responsible for.
 */
public final class GsonManager {

    private final static Object gsonLock = new Object();
    private final static Object jsonParserLock = new Object();

    private volatile static Gson gsonInstance = null;
    private volatile static JsonParser jsonParserInstance = null;

    private GsonManager() {
    }

    /**
     * Return an unique instance of Gson, performing a lazy initialization if no instance exists yet.
     *
     * @return An unique instance of Gson.
     */
    public static Gson getGsonInstance() {
        if (gsonInstance == null) {
            synchronized (gsonLock) {
                if (gsonInstance == null) {
                    gsonInstance = new GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create();
                }
            }
        }
        return gsonInstance;
    }

    /**
     * Return an unique instance of JsonParser, performing a lazy initialization if no instance exists yet.
     *
     * @return An unique instance of JsonParser.
     */
    public static JsonParser getJsonParserInstance() {
        if (jsonParserInstance == null) {
            synchronized (jsonParserLock) {
                if (jsonParserInstance == null) {
                    jsonParserInstance = new JsonParser();
                }
            }
        }
        return jsonParserInstance;
    }

}

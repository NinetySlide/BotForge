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

import com.ninetyslide.libs.feta.common.Constants;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

/**
 * Class that provides facilities to make low level HTTPS POST request.
 */
public final class NetworkManager {

    private final static String METHOD_GET = "GET";
    private final static String METHOD_POST = "POST";

    private NetworkManager() {
    }

    /**
     * Method used to perform a basic HTTPS POST request using the provided URL and the provided body content.
     *
     * @param urlStr The URL to use to make the POST request.
     * @param requestBodyStr The String that will be used as the request body.
     * @return The response returned by the server, or null if an error occurred.
     */
    public static String performPostRequest(String urlStr, String requestBodyStr) {
        return performHttpsRequest(METHOD_POST, urlStr, requestBodyStr);
    }

    /**
     * Method used to perform a basic HTTPs GET request using the provided URL.
     *
     * @param urlStr The URL to use to make the GET request.
     * @return The response returned by the server, or null if an error occurred.
     */
    public static String performGetRequest(String urlStr) {
        return performHttpsRequest(METHOD_GET, urlStr, null);
    }

    /**
     * Main method used to perform a generic HTTPS request (POST or GET) using the provided URL and the provided body
     * content (if applicable).
     *
     * @param method The HTTP method used to perform the request (either POST or GET).
     * @param urlStr The URL to use to make the GET request.
     * @param requestBodyStr The String that will be used as the request body, in case of POST request.
     * @return The response returned by the server, or null if an error occurred.
     */
    private static String performHttpsRequest(String method, String urlStr, String requestBodyStr) {
        HttpsURLConnection connection = null;

        try {
            // Create a new URL
            URL url = new URL(urlStr);

            // Create a new connection and set the headers
            connection = (HttpsURLConnection) url.openConnection();

            // Different behaviours for POST and GET methods
            if (method.equals(METHOD_POST)) {
                connection.setRequestMethod(METHOD_POST);
                connection.setRequestProperty("Content-Type", Constants.HTTP_CONTENT_TYPE_JSON);
                connection.setRequestProperty("Content-Length", Integer.toString(requestBodyStr.getBytes().length));
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                // Send POST data
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(requestBodyStr);
                wr.close();
            } else {
                connection.setRequestMethod(METHOD_GET);
                connection.setUseCaches(false);
                connection.setDoOutput(true);
            }

            // Parse the response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();

            // Return the response
            return response.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

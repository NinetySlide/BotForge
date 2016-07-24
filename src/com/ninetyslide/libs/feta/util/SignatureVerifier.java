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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Class holding static methods related to the verification of the signature in the callbacks.
 */
public class SignatureVerifier {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /**
     * Verify the HMAC-SHA1 signature of the received payload.
     *
     * @param payload The payload to calculate the signature on.
     * @param signature The signature to verify.
     * @param appSecretKey The secret key to calculate the signature.
     * @return True if everything is ok and the signature is verified, false otherwise.
     */
    public static boolean verifySignature(String payload, String signature, String appSecretKey) {
        SecretKeySpec signingKey = new SecretKeySpec(appSecretKey.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = null;

        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return false;
        }

        return signature.equals(toHexString(mac.doFinal(payload.getBytes())));
    }

    /**
     * Convert the byte array in a Hex String.
     *
     * @param bytes The byte array to convert.
     * @return An Hex String representing the byte array.
     */
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

}

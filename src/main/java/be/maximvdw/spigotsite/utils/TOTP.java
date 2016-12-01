/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package be.maximvdw.spigotsite.utils;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * TOTP generator
 * <p>
 * Created by Maxim Van de Wynckel on 01-Dec-16.
 */
public class TOTP {
    public static final String HMAC_SHA1 = "HmacSHA1";

    public static final String HMAC_SHA256 = "HmacSHA256";

    public static final String HMAC_SHA512 = "HmacSHA512";

    // 0 1 2 3 4 5 6 7 8
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    protected static int TIME_SLICE_X = 30000;
    protected static int TIME_ZERO = 0;

    /**
     * Generate a TOTP value using HMAC_SHA1
     *
     * @param key
     * @param returnDigits
     * @return
     * @throws GeneralSecurityException
     */
    public static String generateTOTP(String key, int returnDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();
        long timeInMilis = currentDateTime.getTimeInMillis();

        String steps = "0";
        long T = (timeInMilis - TIME_ZERO) / TIME_SLICE_X;
        steps = Long.toHexString(T).toUpperCase();

        // Just get a 16 digit string
        while (steps.length() < 16)
            steps = "0" + steps;
        return TOTP.generateTOTP(key, steps.toLowerCase(), returnDigits);
    }

    /**
     * Generate a TOTP value using HMAC_SHA256
     *
     * @param key
     * @param returnDigits
     * @return
     * @throws GeneralSecurityException
     */
    public static String generateTOTP256(String key, int returnDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();
        long timeInMilis = currentDateTime.getTimeInMillis();

        String steps = "0";
        long T = (timeInMilis - TIME_ZERO) / TIME_SLICE_X;
        steps = Long.toHexString(T).toUpperCase();

        // Just get a 16 digit string
        while (steps.length() < 16)
            steps = "0" + steps;
        return TOTP.generateTOTP256(key, steps, returnDigits);
    }

    /**
     * Generate a TOTP value using HMAC_SHA512
     *
     * @param key
     * @param returnDigits
     * @return
     * @throws GeneralSecurityException
     */
    public static String generateTOTP512(String key, int returnDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();
        long timeInMilis = currentDateTime.getTimeInMillis();

        String steps = "0";
        long T = (timeInMilis - TIME_ZERO) / TIME_SLICE_X;
        steps = Long.toHexString(T).toUpperCase();

        // Just get a 16 digit string
        while (steps.length() < 16)
            steps = "0" + steps;
        return TOTP.generateTOTP512(key, steps, returnDigits);
    }

    /**
     * This method generates an TOTP value for the given set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes {@link truncationDigits} digits
     * @throws GeneralSecurityException
     */
    public static String generateTOTP(String key, String time, int returnDigits) throws GeneralSecurityException {
        return generateTOTP(key, time, returnDigits, HMAC_SHA1);
    }

    /**
     * This method generates an TOTP value for the given set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes {@link truncationDigits} digits
     * @throws GeneralSecurityException
     */
    public static String generateTOTP256(String key, String time, int returnDigits) throws GeneralSecurityException {
        return generateTOTP(key, time, returnDigits, HMAC_SHA256);
    }

    /**
     * This method generates an TOTP value for the given set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes {@link truncationDigits} digits
     * @throws GeneralSecurityException
     */
    public static String generateTOTP512(String key, String time, int returnDigits) throws GeneralSecurityException {
        return generateTOTP(key, time, returnDigits, HMAC_SHA512);
    }

    /**
     * This method generates an TOTP value for the given set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @param crypto       the crypto function to use
     * @return A numeric String in base 10 that includes digits
     * @throws GeneralSecurityException
     */
    public static String generateTOTP(String key, String time, int returnDigits, String crypto) throws GeneralSecurityException {
        String result = null;
        byte[] hash;

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Complaint with base RFC 4226 (HOTP)
        while (time.length() < 16)
            time = "0" + time;

        // Get the HEX in a Byte[]
        byte[] msg = hexStr2Bytes(time);
        // Adding one byte to get the right conversion
        // byte[] k = hexStr2Bytes(key);
        byte[] k = hexStr2Bytes(key);
        hash = hmac_sha1(crypto, k, msg);
        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[returnDigits];

        result = Integer.toString(otp);
        while (result.length() < returnDigits) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * This method uses the JCE to provide the crypto algorithm. HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto   the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     * @param keyBytes the bytes to use for the HMAC key
     * @param text     the message or text to be authenticated.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static byte[] hmac_sha1(String crypto, byte[] keyBytes, byte[] text) throws GeneralSecurityException {
        Mac hmac;
        hmac = Mac.getInstance(crypto);
        SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
        hmac.init(macKey);
        return hmac.doFinal(text);
    }

    /**
     * This method converts HEX string to Byte[]
     *
     * @param hex the HEX string
     * @return A byte array
     */
    private static byte[] hexStr2Bytes(String hex) {
        // Adding one byte to get the right conversion
        // values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; i++)
            ret[i] = bArray[i + 1];
        return ret;
    }

    private static String bytes2hexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().toLowerCase();
    }

    private static long TIME_INTERVAL = 30 * 1000; // 30 secs

    private static TimeTracker timeTracker = null;

    public static Calendar getCalendar() {
        if (timeTracker == null) {
            timeTracker = new TimeTracker() {
                public Calendar getCalendar() {
                    TimeZone utc = TimeZone.getTimeZone("UTC");
                    return Calendar.getInstance(utc);
                }
            };
        }
        return timeTracker.getCalendar();
    }

    /**
     * Allow integrating applications to set the {@link Calendar} if desired. By default, Calendar for timezone UTC is used
     *
     * @param tt
     */
    public static void setTimeTracker(TimeTracker tt) {
        timeTracker = tt;
    }

    /**
     * Validate a submitted OTP string
     *
     * @param submittedOTP OTP string to validate
     * @param secret       Shared secret
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean validate(String submittedOTP, byte[] secret, int numDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();

        String generatedTOTP = TOTP.generateTOTP(new String(secret), numDigits);
        boolean result = generatedTOTP.equals(submittedOTP);

        if (!result) {
            // Step back time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis -= TIME_INTERVAL;

            String steps = "0";
            long T = (timeInMilis - TOTP.TIME_ZERO) / TOTP.TIME_SLICE_X;
            steps = Long.toHexString(T).toUpperCase();

            // Just get a 16 digit string
            while (steps.length() < 16)
                steps = "0" + steps;

            generatedTOTP = TOTP.generateTOTP(new String(secret), "" + steps, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        if (!result) {
            // Step ahead time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis += TIME_INTERVAL;

            String steps = "0";
            long T = (timeInMilis - TOTP.TIME_ZERO) / TOTP.TIME_SLICE_X;
            steps = Long.toHexString(T).toUpperCase();

            // Just get a 16 digit string
            while (steps.length() < 16)
                steps = "0" + steps;

            generatedTOTP = TOTP.generateTOTP(new String(secret), "" + steps, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        return result;
    }

    /**
     * Validate a submitted OTP string using HMAC_256
     *
     * @param submittedOTP OTP string to validate
     * @param secret       Shared secret
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean validate256(String submittedOTP, byte[] secret, int numDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();

        String generatedTOTP = TOTP.generateTOTP256(new String(secret), numDigits);
        boolean result = generatedTOTP.equals(submittedOTP);

        if (!result) {
            // Step back time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis -= TIME_INTERVAL;

            generatedTOTP = TOTP.generateTOTP256(new String(secret), "" + timeInMilis, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        if (!result) {
            // Step ahead time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis += TIME_INTERVAL;

            generatedTOTP = TOTP.generateTOTP256(new String(secret), "" + timeInMilis, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        return result;
    }

    /**
     * Validate a submitted OTP string using HMAC_512
     *
     * @param submittedOTP OTP string to validate
     * @param secret       Shared secret
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean validate512(String submittedOTP, byte[] secret, int numDigits) throws GeneralSecurityException {
        Calendar currentDateTime = getCalendar();

        String generatedTOTP = TOTP.generateTOTP512(new String(secret), numDigits);
        boolean result = generatedTOTP.equals(submittedOTP);

        if (!result) {
            // Step back time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis -= TIME_INTERVAL;

            generatedTOTP = TOTP.generateTOTP512(new String(secret), "" + timeInMilis, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        if (!result) {
            // Step ahead time interval
            long timeInMilis = currentDateTime.getTimeInMillis();
            timeInMilis += TIME_INTERVAL;

            generatedTOTP = TOTP.generateTOTP512(new String(secret), "" + timeInMilis, numDigits);
            result = generatedTOTP.equals(submittedOTP);
        }

        return result;
    }

    /**
     * Allow integrating applications to set the {@link Calendar} if desired. By default, Calendar for timezone UTC is used
     *
     * @param tt
     */
    public interface TimeTracker {
        Calendar getCalendar();
    }
}
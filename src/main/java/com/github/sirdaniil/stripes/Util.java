package com.github.sirdaniil.stripes;

import java.io.*;
import java.security.*;

/**
 * Created by Daniil Sosonkin
 * 3/28/2017 10:46 PM
 */
public class Util
    {
        private static final String HEX_DIGITS = "0123456789abcdef";

        public static String sha1(String value)
            {
                if (value == null)
                    return null;

                try
                    {
                        return sha1(value.getBytes("ISO-8859-1"));
                    }
                catch (UnsupportedEncodingException e)
                    {
                        return sha1(value.getBytes());
                    }
            }

        public static String sha1(byte[] value)
            {
                if (value == null)
                    return null;

                try
                    {
                        MessageDigest md = MessageDigest.getInstance("SHA-1");
                        md.update(value);
                        byte[] sha1 = md.digest();

                        return toHexString(sha1);
                    }
                catch (NoSuchAlgorithmException e)
                    {
                        return null;
                    }
            }

        public static String sha256(String value)
            {
                if (value == null)
                    return null;

                try
                    {
                        return sha256(value.getBytes("ISO-8859-1"));
                    }
                catch (UnsupportedEncodingException e)
                    {
                        return sha256(value.getBytes());
                    }
            }

        public static byte[] sha256digest(byte[] value)
            {
                if (value == null)
                    return null;

                try
                    {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        md.update(value);

                        return md.digest();
                    }
                catch (NoSuchAlgorithmException e)
                    {
                        return null;
                    }
            }

        public static String sha256(byte[] value)
            {
                if (value == null)
                    return null;

                return toHexString(sha256digest(value));
            }

        public static String toHexString(byte[] v)
            {
                StringBuilder sb = new StringBuilder(v.length * 2);
                for (int i = 0; i < v.length; i++)
                    {
                        int b = v[i] & 0xFF;
                        sb.append(HEX_DIGITS.charAt(b >>> 4));
                        sb.append(HEX_DIGITS.charAt(b & 0xF));
                    }

                return sb.toString();
            }
    }

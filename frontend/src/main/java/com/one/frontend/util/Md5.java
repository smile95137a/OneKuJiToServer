package com.one.frontend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Md5 {
    public static String MD5(String s) {
        char[] hexDigits = "0123456789abcdef".toCharArray();
        try {
            byte[] strTemp = s.getBytes(StandardCharsets.UTF_8); // 固定 UTF-8
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            byte[] md = mdTemp.digest(strTemp);
            char[] str = new char[md.length * 2];
            int k = 0;
            for (byte b : md) {
                str[k++] = hexDigits[(b >>> 4) & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }
}

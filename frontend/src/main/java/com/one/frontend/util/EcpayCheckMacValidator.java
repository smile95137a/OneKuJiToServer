package com.one.frontend.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class EcpayCheckMacValidator {
    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private static final String HASH_IV = "EkRm7iFT261dpevs";

    public boolean validateCheckMacValue(Map<String, String> paymentData, String checkMacValue) {
        // 1. 排序參數（按英文字母 A-Z 排序）
        Map<String, String> sortedParams = new TreeMap<>(paymentData);
        
        // 2. 轉換成 key=value 並用 & 串接
        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (!"CheckMacValue".equals(entry.getKey())) { // 排除 CheckMacValue
                paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        
        // 3. 加入 HashKey 與 HashIV
        String rawData = "HashKey=" + HASH_KEY + "&" + paramString.toString() + "HashIV=" + HASH_IV;
        
        // 4. URL Encode (RFC 1866)
        String encodedData = urlEncode(rawData).toLowerCase();
        
        // 5. SHA256 加密
        String calculatedMac = encryptSHA256(encodedData).toUpperCase();
        
        // 6. 比對 CheckMacValue
        return calculatedMac.equals(checkMacValue);
    }

    private String urlEncode(String data) {
        try {
            return URLEncoder.encode(data, StandardCharsets.UTF_8.toString())
                    .replace("%21", "!")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2A", "*")
                    .replace("%20", "+");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL Encoding failed", e);
        }
    }

    private String encryptSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 encryption failed", e);
        }
    }
}

package com.one.frontend.controller;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AfteeChecksumUtils {

    public static String generateChecksum(Map<String, Object> paymentData, String secretKey) {
        try {
            // Step 1: 排序
            Map<String, Object> sorted = sortRecursively(paymentData);

            // Step 2: 平坦化值
            StringBuilder values = new StringBuilder();
            flattenValues(sorted, values);

            // Step 3: 字串結合密鑰
            String toHash = secretKey + "," + values;

            // Step 4: HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(toHash.getBytes(StandardCharsets.UTF_8));

            // Step 5: Base64 編碼
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("AFTEE Checksum 產生失敗", e);
        }
    }

    private static Map<String, Object> sortRecursively(Map<String, Object> input) {
        Map<String, Object> sortedMap = new TreeMap<>();
        input.forEach((k, v) -> {
            if (v instanceof Map) {
                sortedMap.put(k, sortRecursively((Map<String, Object>) v));
            } else {
                sortedMap.put(k, v);
            }
        });
        return sortedMap;
    }

    private static void flattenValues(Object obj, StringBuilder sb) {
        if (obj instanceof Map<?, ?> map) {
            map.forEach((k, v) -> flattenValues(v, sb));
        } else if (obj instanceof Iterable<?> iterable) {
            iterable.forEach(item -> flattenValues(item, sb));
        } else if (obj != null) {
            sb.append(obj.toString());
        }
    }
}

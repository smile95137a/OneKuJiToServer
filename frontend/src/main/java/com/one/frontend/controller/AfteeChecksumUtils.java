package com.one.frontend.controller;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AfteeChecksumUtils {

//    public static String generateChecksum(Map<String, Object> payment, String secretKey) {
//        try {
//            Map<String, Object> paymentData = new HashMap<>();
//            paymentData.put("amount", payment.get("amount"));
//            paymentData.put("user_no", payment.get("user_no"));
//            paymentData.put("sales_settled", payment.get("sales_settled"));
//            paymentData.put("description_trans", payment.get("description_trans"));
//            paymentData.put("customer", payment.get("customer"));
//            paymentData.put("dest_customers", payment.get("dest_customers"));
//            paymentData.put("items", payment.get("items"));
//            paymentData.put("validation_datetime", payment.get("validation_datetime"));
//
//            // Step 1: 排序 Map（list 處理成模仿前端）
//            Object sorted = sortRecursively(paymentData);
//
//            // Step 2: flatten values
//            StringBuilder values = new StringBuilder();
//            flattenValues(sorted, values);
//
//            // Step 3: 拼接
//            String finalString = secretKey + "," + values.toString();
//
//            // Step 4: sha256(finalString)
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hashBytes = digest.digest(finalString.getBytes(StandardCharsets.UTF_8));
//
//            // Step 5: bytes -> hex string
//            String hexString = bytesToHex(hashBytes);
//
//            // Step 6: hex string 用 latin1 -> base64
//            String base64Checksum = Base64.getEncoder().encodeToString(hexString.getBytes(StandardCharsets.ISO_8859_1));
//
//            return base64Checksum;
//        } catch (Exception e) {
//            throw new RuntimeException("AFTEE Checksum 產生失敗", e);
//        }
//    }

    public static String generateChecksum(Map<String, Object> payment, String secretKey) {
        try {
            // 印出原本的 payment
            System.out.println("payment = " + payment);

            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("amount", payment.get("amount"));
            paymentData.put("user_no", payment.get("user_no"));
            paymentData.put("sales_settled", payment.get("sales_settled"));
            paymentData.put("description_trans", payment.get("description_trans"));
            paymentData.put("customer", payment.get("customer"));
            paymentData.put("dest_customers", payment.get("dest_customers"));
            paymentData.put("items", payment.get("items"));
            paymentData.put("validation_datetime", payment.get("validation_datetime"));

            // Step 1: 排序 Map（list 處理成模仿前端）
            Object sorted = sortRecursively(paymentData);

            // Step 2: flatten values
            StringBuilder values = new StringBuilder();
            flattenValues(sorted, values);

            // 印出 values
            System.out.println("flattened values = " + values);

            // Step 3: 拼接
            String finalString = secretKey + "," + values.toString();
            System.out.println("拼接起來:" + finalString);
            // Step 4: sha256(finalString)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(finalString.getBytes(StandardCharsets.UTF_8));

            // Step 5: bytes -> hex string
            String hexString = bytesToHex(hashBytes);

            // Step 6: hex string 用 latin1 -> base64
            String base64Checksum = Base64.getEncoder().encodeToString(hexString.getBytes(StandardCharsets.ISO_8859_1));
            System.out.println("base64回傳直:" + base64Checksum);
            return base64Checksum;
        } catch (Exception e) {
            throw new RuntimeException("AFTEE Checksum 產生失敗", e);
        }
    }


    // 專門處理排序：Map Key 排序，List 只處理元素
    private static Object sortRecursively(Object input) {
        if (input instanceof Map<?, ?> mapInput) {
            Map<String, Object> sortedMap = new TreeMap<>();
            for (Map.Entry<?, ?> entry : mapInput.entrySet()) {
                sortedMap.put(entry.getKey().toString(), sortRecursively(entry.getValue()));
            }
            return sortedMap;
        } else if (input instanceof List<?> listInput) {
            List<Object> newList = new ArrayList<>();
            for (Object item : listInput) {
                newList.add(sortRecursively(item));
            }
            // 注意：這裡不做排序！直接原順序
            return newList;
        } else {
            return input;
        }
    }

    // 將 Object 平坦化，符合前端 flatten 規則
    private static void flattenValues(Object obj, StringBuilder sb) {
        if (obj instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                flattenValues(value, sb);
            }
        } else if (obj instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                flattenValues(item, sb);
            }
        } else if (obj != null) {
            sb.append(obj.toString());
        }
        // 注意：null 在前端是會跳過的，這裡保持不動
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

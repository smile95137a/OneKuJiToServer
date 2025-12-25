package com.one.test;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Test {

    public static void main(String[] args) {
        String url = "https://logistics.gomypay.asia/Logisticstm.aspx";

        // 設定 POST 的參數
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("Url", "https://api.localhost.tw:8081/logistics/callback");
//        params.add("Url", "http://localhost:8081/logistics/callback");
        params.add("Opmode", "3");

        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // 處理回應
        System.out.println("Response: " + response);
    }
}

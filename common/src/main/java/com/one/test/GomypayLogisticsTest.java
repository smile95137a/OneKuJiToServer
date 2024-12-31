package com.one.test;

import com.one.util.Md5;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GomypayLogisticsTest {

    public static String main(String[] args) {
        // 設定要發送的 URL
        String url = "https://logistics.gomypay.asia/LogisticsAPI.aspx";
        String md5 = "5E11B0983580ABDE" + "123456789";
        String s = Md5.MD5(md5.toLowerCase());
        s = s.toUpperCase();
        // 設定 POST 的參數
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("Vendororder", "123456789"); // 客戶訂單編號
        params.add("mode", "C2C"); // 物流方式
        params.add("EshopId", "0038"); // 客戶代號
        params.add("StoreId", "276649"); // 門市代號
        params.add("Amount", "1"); // 交易金額
        params.add("ServiceType", "1"); // 服務型態代碼
        params.add("OrderAmount", "1000"); // 商品價值
        params.add("SenderName", "張三"); // 寄件人姓名
        params.add("SendMobilePhone", "0912345678"); // 寄件人手機電話
        params.add("ReceiverName", "李四"); // 取貨人姓名
        params.add("ReceiverMobilePhone", "0987654321"); // 取貨人手機電話
        params.add("OPMode", "3"); // 通路代號
        params.add("Internetsite", "https://api.onemorelottery.tw/logistics/callback"); // 接收狀態的網址
        params.add("ShipDate", "2024-10-09"); // 出貨日期
        params.add("CHKMAC", s); // 檢查碼

        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // 處理回應
        System.out.println("Response: " + response.getBody());

        return response.getBody();
    }
}

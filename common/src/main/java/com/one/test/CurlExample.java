package com.one.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CurlExample {
    public static void main(String[] args) {
        String url = "https://n.gomypay.asia/TestShuntClass.aspx";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder requestBody = new StringBuilder();
            requestBody.append("Send_Type=4")
                       .append("&Pay_Mode_No=2")
                       .append("&CustomerId=B82FD0DF7DE03FC702DEC35A2446E469")
                       .append("&Order_No=")
                       .append("&Amount=100") // 示例金额
//                       .append("&TransCode=")
                       .append("&Buyer_Name=jimmy")
                       .append("&Buyer_Telm=0970124936")
                       .append("&Buyer_Mail=smile3541a@gmail.com")
                       .append("&Buyer_Memo=再來一抽備註")
//                       .append("&CardNo=4907060600015101")
//                       .append("&ExpireDate=2412") // 示例有效期
//                       .append("&CVV=615")
//                       .append("&TransMode=1")
//                       .append("&Installment=0")
//                       .append("&Return_url=")
                       .append("&Callback_Url=https://api.onemorelottery.tw/payment/paymentCallback")
//                       .append("&e_return=1")
                    .append("&e_return=1")
                       .append("&Str_Check=d0q2mo1729enisehzolmhdwhkac38itb");

            post.setEntity(new StringEntity(requestBody.toString()));

            HttpResponse response = httpClient.execute(post);
            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

            // 读取响应内容
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Response JSON: " + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
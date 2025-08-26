package com.one.onekuji.controller;

import com.google.gson.Gson;
import com.one.onekuji.model.EshopOrderEntity;
import com.one.onekuji.model.VendorOrderEntity;
import com.one.onekuji.repository.CustomerOrderRepository;
import com.one.onekuji.repository.EshopOrderRepository;
import com.one.onekuji.repository.VendorOrderRepository;
import com.one.onekuji.request.Address;
import com.one.onekuji.request.CallHome;
import com.one.onekuji.request.HomeReq;
import com.one.onekuji.request.LogisticsRequest;
import com.one.onekuji.util.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpressService {

    @Autowired
    private VendorOrderRepository vendorOrderRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private EshopOrderRepository eshopOrderRepository;

    public String convenience(LogisticsRequest logisticsRequest) {
        String url = "https://logistics.gomypay.asia/LogisticsAPI.aspx";
        String md5 = "5E11B0983580ABDE" + logisticsRequest.getVendorOrder();
        String s = Md5.MD5(md5.toLowerCase());
        s = s.toUpperCase();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("Vendororder", logisticsRequest.getVendorOrder()); // 客戶訂單編號
        params.add("mode", "C2C"); // 物流方式
        params.add("EshopId", "0038"); // 客戶代號
        params.add("StoreId", logisticsRequest.getStoreId()); // 門市代號
        params.add("Amount", logisticsRequest.getAmount()); // 交易金額
        params.add("ServiceType", "3"); // 服務型態代碼 //1- 取貨付款3- 取貨不付款退貨通5- 退貨付款4- 退貨不付款//通路代號 1:全家 2:萊爾富3: 統一超商 4.OK 超商
        params.add("OrderAmount", logisticsRequest.getOrderAmount()); // 商品價值
        params.add("SenderName", logisticsRequest.getSenderName()); // 寄件人姓名
        params.add("SendMobilePhone", logisticsRequest.getSendMobilePhone()); // 寄件人手機電話
        params.add("ReceiverName", logisticsRequest.getReceiverName()); // 取貨人姓名
        params.add("ReceiverMobilePhone", logisticsRequest.getReceiverMobilePhone()); // 取貨人手機電話
        params.add("OPMode", logisticsRequest.getOpMode()); // 通路代號
        params.add("Internetsite", "https://api.onemorelottery.tw:8081/logistics/callback"); // 接收狀態的網址
        params.add("ShipDate", logisticsRequest.getShipDate()); // 出貨日期
        params.add("CHKMAC", s); // 檢查碼

        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String jsonResponse = response.getBody();
        System.out.println(jsonResponse);
        System.out.println("123213");
// 如果返回的数据是一个字符串，而不是 JSON
        if (jsonResponse != null && !jsonResponse.startsWith("{")) {
            // 使用正則表達式提取 VendorOrder, OrderNo, ErrorCode, 和 ErrorMessage
            String regex = "Vendororder=(.*?),OrderNo=(.*?),ErrorCode=(.*?),ErrorMessage=(.*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                // 提取相應的值
                String vendorOrder = matcher.group(1);
                String orderNo = matcher.group(2);
                String errorCode = matcher.group(3);
                String errorMessage = matcher.group(4);

                // 只有在 ErrorCode 為 000 的時候才插入資料庫
                if ("000".equals(errorCode)) {
                    VendorOrderEntity vendorOrderEntity = new VendorOrderEntity();
                    vendorOrderEntity.setVendorOrder(vendorOrder);
                    vendorOrderEntity.setOrderNo(orderNo);
                    vendorOrderEntity.setErrorCode(errorCode);
                    vendorOrderEntity.setErrorMessage(errorMessage);
                    vendorOrderEntity.setExpress("1".equals(logisticsRequest.getOpMode()) ? "全家" : "711");
                    vendorOrderEntity.setStatus("未寄出");

                    // 插入資料庫
                    vendorOrderRepository.insert(vendorOrderEntity);
                    System.out.println("已插入資料庫");
                } else {
                    System.out.println("訂單失敗，錯誤代碼：" + errorCode);
                }
            } else {
                System.out.println("無法解析響應數據");
            }
        } else {
            // 如果返回的是 JSON 格式
            Gson gson = new Gson();
            VendorOrderEntity vendorOrderEntity = gson.fromJson(jsonResponse, VendorOrderEntity.class);
            vendorOrderEntity.setExpress("1".equals(logisticsRequest.getOpMode()) ? "全家" : "711");
            vendorOrderRepository.insert(vendorOrderEntity);
        }
        return jsonResponse;

    }

    public String home(HomeReq homeReq) {

        String url = "https://logistics.gomypay.asia/Api/Delivery/PrintOBT.aspx"; //正式
//        String url = "http://testlogistics.gomytw.com/Api/Delivery/PrintOBT.aspx"; //測試
        String md5 = "5E11B0983580ABDE" + homeReq.getVendorOrder();
        String s = Md5.MD5(md5.toLowerCase());
        s = s.toUpperCase();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("VendorOrder", homeReq.getVendorOrder()); // 客戶訂單編號
        params.add("EshopId", "0038"); // 客戶代號
        params.add("Thermosphere", "0001"); // 溫層(代碼)
        params.add("Spec", String.valueOf(homeReq.getSpec())); // 規格(代碼)
        params.add("ServiceType", "3"); // 服務型態代碼
        params.add("InternetSite", ""); // 接收狀態的網址
        params.add("Amount", String.valueOf(homeReq.getAmount())); // 交易金額
        params.add("OrderAmount", homeReq.getOrderAmount()); // 商品價值
        params.add("RecipientName", homeReq.getRecipientName()); // 取貨人姓名
        params.add("RecipientMobile", homeReq.getRecipientMobile()); // 取貨人手機電話
        params.add("RecipientAddress", homeReq.getRecipientAddress()); // 取貨人地址
        params.add("SenderName", homeReq.getSenderName()); // 寄件人姓名
        params.add("SenderMobile", homeReq.getSenderMobile()); // 寄件人手機電話
        params.add("SenderZipCode", homeReq.getSenderZipCode()); // 寄件人郵碼
        params.add("SenderAddress", homeReq.getSenderAddress()); // 寄件人地址
        params.add("ShipmentDate", homeReq.getShipmentDate()); // 出貨日期
        params.add("DeliveryDate", homeReq.getDeliveryDate()); // 希望配達日期
        params.add("DeliveryTime" , homeReq.getDeliveryTime());
        params.add("ProductTypeId", "0015"); // 商品類別(代碼)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.add("PrintDateTime", sdf.format(new Date()));
        params.add("ProductName", "景品"); // 商品名稱
        params.add("CHKMAC", s); // 檢查碼

        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println(key + ": " + values);
        }
        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String jsonResponse = response.getBody();
        System.out.println(jsonResponse);
        System.out.println("123213");
// 如果返回的数据是一个字符串，而不是 JSON
        if (jsonResponse != null && !jsonResponse.startsWith("{")) {
            // 使用正則表達式提取 VendorOrder, OrderNo, ErrorCode, 和 ErrorMessage
            String regex = "Vendororder=(.*?),OrderNo=(.*?),ErrorCode=(.*?),ErrorMessage=(.*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                // 提取相應的值
                String vendorOrder = matcher.group(1);
                String orderNo = matcher.group(2);
                String errorCode = matcher.group(3);
                String errorMessage = matcher.group(4);

                // 只有在 ErrorCode 為 000 的時候才插入資料庫
                if ("000".equals(errorCode)) {
                    VendorOrderEntity vendorOrderEntity = new VendorOrderEntity();
                    vendorOrderEntity.setVendorOrder(vendorOrder);
                    vendorOrderEntity.setOrderNo(orderNo);
                    vendorOrderEntity.setErrorCode(errorCode);
                    vendorOrderEntity.setErrorMessage(errorMessage);
                    vendorOrderEntity.setExpress("黑貓");
                    // 插入資料庫
                    vendorOrderRepository.insert(vendorOrderEntity);
                    System.out.println("已插入資料庫");
                } else {
                    System.out.println("訂單失敗，錯誤代碼：" + errorCode);
                }
            } else {
                System.out.println("無法解析響應數據");
            }
        } else {
            // 如果返回的是 JSON 格式
            Gson gson = new Gson();
            VendorOrderEntity vendorOrderEntity = gson.fromJson(jsonResponse, VendorOrderEntity.class);
            vendorOrderEntity.setExpress("黑貓");
            vendorOrderRepository.insert(vendorOrderEntity);
        }
        return jsonResponse;

    }

    public String getAddress(Address address) {
        String url = "https://logistics.gomypay.asia/Api/Delivery/ParsingAddress.aspx"; //正式
//        String url = "http://testlogistics.gomytw.com/Api/Delivery/ParsingAddress.aspx"; //測試

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("EshopId", "0038"); // 客戶代號
        params.add("Addresses", address.getAddress()); // 地址
        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();

    }

    public String callHome(CallHome callHome) {
        //String url = "https://logistics.gomypay.asia/Api/Delivery/Call.aspx"; //正式
        String url = "http://testlogistics.gomytw.com/Api/Delivery/Call.aspx"; //測試
        String md5 = "5E11B0983580ABDE" + callHome.getVendorOrder();
        String s = Md5.MD5(md5.toLowerCase());
        s = s.toUpperCase();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("EshopId", "0038"); // 客戶代號
        params.add("ContactName", callHome.getContactName()); // 聯絡人姓名
        params.add("ContactMobile", callHome.getContactMobile()); // 聯絡人手機
        params.add("ContactAddress", callHome.getContactAddress()); // 聯絡人地址
        params.add("NormalQuantity", String.valueOf(callHome.getNormalQuantity())); // 常溫出貨件數
        params.add("ColdQuantity", ""); // 冷藏出貨件數
        params.add("FreezeQuantity", ""); // 冷凍出貨件數
        params.add("IsContact", callHome.getIsContact()); // 是否需要電聯
        params.add("IsTrolley", callHome.getIsTrolley()); // 是否需要推車
        params.add("CHKMAC", s); // 檢查碼

        // 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

        // 封裝請求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 發送 POST 請求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String jsonResponse = response.getBody();
        System.out.println(jsonResponse);
// 如果返回的数据是一个字符串，而不是 JSON
        if (jsonResponse != null && !jsonResponse.startsWith("{")) {
            // 使用正則表達式提取 EshopId, ErrorCode, 和 ErrorMessage
            String regex = "1\\s(\\w{1,20})\\s2\\s(\\w{1,3})\\s3\\s(.+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                // 提取相應的值
                String eshopId = matcher.group(1); // 客戶訂單編號
                String errorCode = matcher.group(2); // 訊息代碼
                String errorMessage = matcher.group(3); // 訊息內容

                // 只有在 ErrorCode 為 000 的時候才插入資料庫
                if ("000".equals(errorCode)) {
                    EshopOrderEntity eshopOrder = new EshopOrderEntity();
                    eshopOrder.setEshopId(eshopId); // 設置客戶訂單編號
                    eshopOrder.setErrorCode(errorCode); // 設置訊息代碼
                    eshopOrder.setErrorMessage(errorMessage); // 設置訊息內容

                    // 插入資料庫
                    eshopOrderRepository.insert(eshopOrder);
                    System.out.println("已插入資料庫");
                } else {
                    System.out.println("訂單失敗，錯誤代碼：" + errorCode);
                }
            } else {
                System.out.println("無法解析響應數據");
            }
        } else {
            // 如果返回的是 JSON 格式
            Gson gson = new Gson();
            EshopOrderEntity eshopOrder = gson.fromJson(jsonResponse, EshopOrderEntity.class);
            eshopOrderRepository.insert(eshopOrder);
        }
        return jsonResponse;

    }
}

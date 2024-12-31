package com.one.service;

import com.one.model.InvoicePictureRequest;
import com.one.repository.UserRepository;
import com.one.request.ReceiptReq;
import com.one.response.ReceiptRes;
import com.one.response.UserRes;
import com.one.util.Md5;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class InvoiceService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;
    // 如果你没有使用 Spring 的依赖注入，也可以手动初始化
    public InvoiceService() {
        this.restTemplate = new RestTemplate();
    }
    public static ReceiptReq generateFakeReceipt() {
        Random random = new Random();

        // 建立一個 ReceiptReq 物件
        ReceiptReq receipt = ReceiptReq.builder()
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .customerName(null)
                .phone(null)
                .orderCode(null)
                .datetime("2024-09-27 12:34:56")
                .email(null)
                .state(0)
                .donationCode(null)
                .taxType(null)
                .companyCode(null)
                .freeAmount(null)
                .zeroAmount(null)
                .sales(null)
                .amount(null)
                .totalFee(String.valueOf(random.nextInt(10000)))
                .content(null)
                .items(generateFakeItems(3))  // 建立一個帶有3個假項目的Item列表
                .build();

        return receipt;
    }

    private static List<ReceiptReq.Item> generateFakeItems(int count) {
        List<ReceiptReq.Item> items = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            ReceiptReq.Item item = ReceiptReq.Item.builder()
                    .name("Item " + i)
                    .money(random.nextInt(1000))
                    .number(random.nextInt(10) + 1)
                    .taxType(null)
                    .remark(null)
                    .build();

            items.add(item);
        }

        return items;
    }

    public static void main(String[] args) {
        ReceiptReq fakeReceipt = generateFakeReceipt();
        InvoiceService service = new InvoiceService();
        System.out.println(fakeReceipt);
        ResponseEntity<ReceiptRes> receiptResResponseEntity = service.addB2CInvoice(fakeReceipt);
        System.out.println(receiptResResponseEntity.getBody());
    }

    public ResponseEntity<ReceiptRes> addB2CInvoice(ReceiptReq invoiceRequest) {
        String url = "https://www.giveme.com.tw/invoice.do?action=addB2C";

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String date = System.currentTimeMillis()+"";
        // 创建一个 DateTimeFormatter，指定日期格式为 "yyyy-MM-dd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        // 将当前日期格式化为字符串
        String formattedDate = currentDate.format(formatter);
        String md5 = Md5.MD5(date+"eason"+"Jj47075614").toUpperCase();
//        String md5 = Md5.MD5(date + "Giveme09" + "6F89Gi").toUpperCase();
        ReceiptReq req = ReceiptReq.builder().timeStamp(date).uncode("47075614").idno("eason")
                .sign(md5).customerName(null).phone(invoiceRequest.getPhone()).orderCode(invoiceRequest.getOrderCode()).datetime(formattedDate).email(invoiceRequest.getEmail()).state(invoiceRequest.getState()).donationCode(invoiceRequest.getDonationCode()).taxType(null).companyCode(null).freeAmount(null).zeroAmount(null).sales(null).totalFee(invoiceRequest.getTotalFee()).content("再來一抽備註").items(invoiceRequest.getItems()).build();
//        ReceiptReq req = ReceiptReq.builder().timeStamp(date).uncode("53418005").idno("Giveme09")
//                .sign(md5).customerName(null).phone(null).orderCode(invoiceRequest.getOrderCode()).datetime(formattedDate).email(invoiceRequest.getEmail()).state(invoiceRequest.getState()).donationCode(invoiceRequest.getDonationCode()).taxType(null).companyCode(null).freeAmount(null).zeroAmount(null).sales(null).totalFee(invoiceRequest.getTotalFee()).content("再來一抽備註").items(invoiceRequest.getItems()).build();
        System.out.println(req);
        // 发送 POST 请求
        try {
            // 直接发送 JSON 对象
            return restTemplate.postForEntity(url, req, ReceiptRes.class); // 处理响应
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e; // 重新抛出异常
        }
    }

    public ResponseEntity<byte[]> getInvoicePicture(String code , Long userId) throws MessagingException {
        String url = "https://www.giveme.com.tw/invoice.do?action=picture";
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String date = System.currentTimeMillis()+"";
       String md5 = Md5.MD5(date+"eason"+"Jj47075614").toUpperCase();
//        String md5 = Md5.MD5(date + "Giveme09" + "6F89Gi").toUpperCase();
        InvoicePictureRequest req = InvoicePictureRequest.builder().timeStamp(date).uncode("47075614").idno("eason").sign(md5).code(code).type("2").build();
//        InvoicePictureRequest req = InvoicePictureRequest.builder().timeStamp(date).uncode("53418005").idno("Giveme09").sign(md5).code(code).type("2").build();
        // 发送 POST 请求
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, req, byte[].class);
        UserRes user = userRepository.getUserById(userId);

        if(user.getInvoiceInfoEmail() == null){
            mailService.sendRecImg(user.getEmail(),response);
        }else{
            mailService.sendRecImg(user.getInvoiceInfoEmail(),response);
        }


        return response;
    }
}

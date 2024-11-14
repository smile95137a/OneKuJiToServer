package com.one.frontend.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.request.ReceiptReq;
import com.one.frontend.response.ReceiptRes;
import com.one.frontend.service.InvoiceService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/add")
    public void addInvoice(@RequestBody ReceiptReq invoiceRequest) throws MessagingException {
        CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
        Long userId = userDetails.getId();
        try {
            ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
            System.out.println(res.getBody());
            ReceiptRes receiptRes = res.getBody();
            invoiceService.getInvoicePicture(receiptRes.getCode() , userId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @GetMapping("/generate-fake")
    public ResponseEntity<ReceiptRes> generateFakeInvoice() throws MessagingException {
        // 生成假發票請求數據
        ReceiptReq fakeReceipt = generateFakeReceipt();

        // 調用 InvoiceService 中的 addB2CInvoice 方法
        ResponseEntity<ReceiptRes> response = invoiceService.addB2CInvoice(fakeReceipt);
        ReceiptRes receiptRes = response.getBody();
        invoiceService.getInvoicePicture(receiptRes.getCode() , 6L);

        // 返回服務響應給客戶端
        return response;
    }

    // 記錄生成假數據的私有方法
    private static ReceiptReq generateFakeReceipt() {
        Random random = new Random();

        return ReceiptReq.builder()
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
                .items(generateFakeItems(3))  // 生成包含 3 個假項目的列表
                .build();
    }

    // 記錄生成假項目的私有方法
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
}

package com.one.frontend.controller;

import com.one.frontend.dto.LogisticsRequest;
import com.one.frontend.model.CvsStoreInfo;
import com.one.frontend.repository.CvsStoreInfoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {

    private final CvsStoreInfoRepository cvsStoreInfoRepository;

    public LogisticsController(CvsStoreInfoRepository cvsStoreInfoRepository) {
        this.cvsStoreInfoRepository = cvsStoreInfoRepository;
    }

    // 接收第三方回调接口
    private static final Logger logger = LoggerFactory.getLogger(LogisticsController.class);
    @PostMapping(value = "/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void handleCallback(@ModelAttribute LogisticsRequest request, HttpServletResponse response) {
        // 打印所有参数
        logger.info("Received LogisticsRequest: {}", request);

        // 如果需要更详细的单字段打印，也可以逐个打印
        logger.info("MerchantID: {}", request.getMerchantID());
        logger.info("MerchantTradeNo: {}", request.getMerchantTradeNo());
        logger.info("LogisticsSubType: {}", request.getLogisticsSubType());
        logger.info("CvsStoreID: {}", request.getCvsStoreID());
        logger.info("CvsStoreName: {}", request.getCvsStoreName());
        logger.info("CvsAddress: {}", request.getCvsAddress());
        logger.info("CvsTelephone: {}", request.getCvsTelephone());
        logger.info("CvsOutside: {}", request.getCvsOutside());
        logger.info("ExtraData: {}", request.getExtraData());

        try {
            String cvsStoreID = (request.getCvsStoreID() != null) ? request.getCvsStoreID() : "defaultStoreID"; // 默认值
            String cvsStoreName = (request.getCvsStoreName() != null) ? request.getCvsStoreName() : "defaultStoreName"; // 默认值
            String cvsAddress = (request.getCvsAddress() != null) ? request.getCvsAddress() : "defaultAddress"; // 默认值
            String cvsTelephone = (request.getCvsTelephone() != null) ? request.getCvsTelephone() : "defaultTelephone"; // 默认值
            // 模拟数据保存逻辑
            CvsStoreInfo storeInfo = new CvsStoreInfo();
            storeInfo.setCvsStoreID(cvsStoreID);
            storeInfo.setCvsStoreName(cvsStoreName);
            storeInfo.setCvsAddress(cvsAddress);
            storeInfo.setCvsTelephone(cvsTelephone);
            storeInfo.setCvsOutside(request.getCvsOutside() != null ? request.getCvsOutside() : "0");
            storeInfo.setUuid(request.getExtraData() != null ? request.getExtraData() : UUID.randomUUID().toString());
            cvsStoreInfoRepository.save(storeInfo);
            // 假设这里有保存操作
            logger.info("Saving store info: {}", storeInfo);

            // 重定向到指定页面并附加 UUID 参数
            String redirectUrl = "http://chichi-player.s3-website.ap-northeast-3.amazonaws.com/cart?uuid=" + storeInfo.getUuid();
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error("Error handling logistics callback", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred");
            } catch (IOException ex) {
                logger.error("Error sending error response", ex);
            }
        }
    }


    // 根据 UUID 查询 CVS 门市数据
    @GetMapping("/{uuid}")
    public ResponseEntity<CvsStoreInfo> getStoreInfoByUUID(@PathVariable String uuid) {
        CvsStoreInfo storeInfo = cvsStoreInfoRepository.findByUuid(uuid);
        if (storeInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(storeInfo);
    }

}

package com.one.frontend.controller;

import com.one.frontend.dto.LogisticsRequest;
import com.one.frontend.model.CvsStoreInfo;
import com.one.frontend.repository.CvsStoreInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> handleCallback(@RequestBody LogisticsRequest request) {
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
            // 模拟数据保存逻辑
            CvsStoreInfo storeInfo = new CvsStoreInfo();
            storeInfo.setCvsStoreID(request.getCvsStoreID());
            storeInfo.setCvsStoreName(request.getCvsStoreName());
            storeInfo.setCvsAddress(request.getCvsAddress());
            storeInfo.setCvsTelephone(request.getCvsTelephone());
            storeInfo.setCvsOutside(request.getCvsOutside() != null ? request.getCvsOutside() : "0");
            storeInfo.setUuid(request.getExtraData() != null ? request.getExtraData() : UUID.randomUUID().toString());

            // 假设这里有保存操作
            logger.info("Saving store info: {}", storeInfo);

            return ResponseEntity.status(HttpStatus.CREATED).body(storeInfo.getUuid());
        } catch (Exception e) {
            logger.error("Error handling logistics callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
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

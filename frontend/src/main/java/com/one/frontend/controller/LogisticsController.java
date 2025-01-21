package com.one.frontend.controller;

import com.one.frontend.dto.LogisticsRequest;
import com.one.frontend.model.CvsStoreInfo;
import com.one.frontend.repository.CvsStoreInfoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {

    private final CvsStoreInfoRepository cvsStoreInfoRepository;

    public LogisticsController(CvsStoreInfoRepository cvsStoreInfoRepository) {
        this.cvsStoreInfoRepository = cvsStoreInfoRepository;
    }

    // 接收第三方回调接口
    @PostMapping(value = "/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleCallback(@ModelAttribute LogisticsRequest request) {
        try {
            CvsStoreInfo storeInfo = new CvsStoreInfo();
            storeInfo.setCvsStoreID(request.getCvsStoreID());
            storeInfo.setCvsStoreName(request.getCvsStoreName());
            storeInfo.setCvsAddress(request.getCvsAddress());
            storeInfo.setCvsTelephone(request.getCvsTelephone());
            storeInfo.setCvsOutside(request.getCvsOutside() != null ? request.getCvsOutside() : "0");
            storeInfo.setUuid(request.getExtraData() != null ? request.getExtraData() : UUID.randomUUID().toString());

            CvsStoreInfo savedStore = cvsStoreInfoRepository.save(storeInfo);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedStore.getUuid());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // 根据 UUID 查询 CVS 门市数据
    @GetMapping("/{uuid}")
    public ResponseEntity<CvsStoreInfo> getStoreInfoByUUID(@PathVariable String uuid) {
        Optional<CvsStoreInfo> storeInfo = cvsStoreInfoRepository.findByUuid(uuid);

        return storeInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

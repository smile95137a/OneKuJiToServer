package com.one.frontend.controller;

import com.one.frontend.dto.LogisticsRequest;
import com.one.frontend.model.CvsStoreInfo;
import com.one.frontend.repository.CvsStoreInfoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {

    private final CvsStoreInfoRepository cvsStoreInfoRepository;

    public LogisticsController(CvsStoreInfoRepository cvsStoreInfoRepository) {
        this.cvsStoreInfoRepository = cvsStoreInfoRepository;
    }

    // 接收第三方回调接口
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@ModelAttribute LogisticsRequest request) {
        // 只存储 CVS 开头的字段
        CvsStoreInfo storeInfo = new CvsStoreInfo();
        storeInfo.setCvsStoreID(request.getCvsStoreID());
        storeInfo.setCvsStoreName(request.getCvsStoreName());
        storeInfo.setCvsAddress(request.getCvsAddress());
        storeInfo.setCvsTelephone(request.getCvsTelephone());
        storeInfo.setCvsOutside(request.getCvsOutside());
        storeInfo.setUuid(request.getExtraData());

        // 保存到数据库
        CvsStoreInfo savedStore = cvsStoreInfoRepository.save(storeInfo);

        // 返回 UUID
        return ResponseEntity.ok(savedStore.getUuid());
    }

    // 根据 UUID 查询 CVS 门市数据
    @GetMapping("/{uuid}")
    public ResponseEntity<CvsStoreInfo> getStoreInfoByUUID(@PathVariable String uuid) {
        Optional<CvsStoreInfo> storeInfo = cvsStoreInfoRepository.findByUuid(uuid);

        return storeInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

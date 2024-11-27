package com.one.onekuji.service;

import com.one.onekuji.model.RedemptionCode;
import com.one.onekuji.repository.RedemptionCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RedemptionCodeService {

    @Autowired
    private RedemptionCodeMapper redemptionCodeMapper;

    // 生成兌換碼
    public String generateRedemptionCode(Long productId, Long count) {
        String code = null;
        List<RedemptionCode> list = new ArrayList<>();
        for(long i = 0 ; i < count; i++ ){
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

            // 確保兌換碼唯一
            while (redemptionCodeMapper.findByCode(code).isPresent()) {
                code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
            }
            RedemptionCode redemptionCode = new RedemptionCode();
            redemptionCode.setCode(code);
            redemptionCode.setIsRedeemed(false);  // 未兌換
            redemptionCode.setProductId(productId);
            list.add(redemptionCode);
        }

        redemptionCodeMapper.insertRedemptionCodes(list);

        return code;
    }

    // 兌換操作
    public List<RedemptionCode> redeemCode() {
        List<RedemptionCode> redemptionCodeOpt = redemptionCodeMapper.findById();
        return redemptionCodeOpt;
    }

    public List<RedemptionCode> redeemCodeByProductId(Long productId) {
        return redemptionCodeMapper.findByProductId(productId);
    }
}

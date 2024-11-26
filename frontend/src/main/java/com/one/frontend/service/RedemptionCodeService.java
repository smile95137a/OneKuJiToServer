package com.one.frontend.service;

import com.one.frontend.dto.DrawDto;
import com.one.frontend.model.RedemptionCode;
import com.one.frontend.repository.RedemptionCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RedemptionCodeService {

    @Autowired
    private RedemptionCodeMapper redemptionCodeMapper;

    // 兌換操作
    public String redeemCode(Long userId , DrawDto drawDto) {
        Optional<RedemptionCode> redemptionCodeOpt = redemptionCodeMapper.findByCode(drawDto.getCode());

        if (redemptionCodeOpt.isPresent()) {
            if (!redemptionCodeOpt.get().getProductId().equals(drawDto.getProductId())) {
                return "此兌換碼並非此商品";
            }
        }

        if (redemptionCodeOpt.isPresent()) {
            RedemptionCode redemptionCode = redemptionCodeOpt.get();
            if (redemptionCode.getIsRedeemed()) {
                return "兌換碼已被使用";
            }


            // 更新兌換碼狀態
            redemptionCode.setRedeemedAt(LocalDateTime.now());
            redemptionCode.setUserId(userId);
            redemptionCodeMapper.updateRedemptionCode(redemptionCode);

            return "兌換成功";
        } else {
            return "兌換碼無效";
        }
    }
}

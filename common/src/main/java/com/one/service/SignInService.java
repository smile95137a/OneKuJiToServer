package com.one.service;

import com.one.frontend.model.DailySignInRecord;
import com.one.frontend.model.SignIn;
import com.one.frontend.repository.DailySignInRepository;
import com.one.frontend.repository.SignInMapper;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.repository.UserTransactionRepository;
import com.one.frontend.response.SignInRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final SignInMapper signInMapper;
    private final DailySignInRepository dailySignInRepository;
    @Autowired
    private UserTransactionRepository userTransactionRepository;
    private final UserRepository userRepository;

    public List<SignIn> getAllSignIns() {
        return signInMapper.findAll();
    }

    public SignInRes spinWheel(Long userId) throws Exception {
        // 获取当月的开始和结束日期
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();  // 2024-11-01 00:00:00
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);  // 2024-11-30 23:59:59.999999999

        // 查询用户当月的储值总金额
        BigDecimal totalDepositAmount = userTransactionRepository.getTotalAmountForUserAndMonth(
                userId, "CONSUME", startOfMonth, endOfMonth);

        // 检查是否达到 1000 元
        if (totalDepositAmount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new Exception("當月消費金額未滿 1000 元，無法簽到");
        }

        // 如果儲值滿 500，則繼續處理簽到邏輯
        List<SignInRes> signIns = signInMapper.findAllByRes();
        double randomValue = Math.random(); // 0.0 到 1.0 之間的隨機數

        double cumulativeProbability = 0.0;
        for (SignInRes signIn : signIns) {
            cumulativeProbability += signIn.getProbability();
            if (randomValue <= cumulativeProbability) {
                // 檢查當天是否已簽到
                DailySignInRecord todayRecord = dailySignInRepository.getRecordByUserIdAndDate(userId, LocalDate.now());

                if (todayRecord == null) {
                    // 插入新的簽到記錄
                    DailySignInRecord newRecord = DailySignInRecord.builder()
                            .userId(userId)
                            .signInDate(LocalDate.now())
                            .rewardPoints(signIn.getSliverPrice())
                            .build();

                    dailySignInRepository.insertSignInRecord(newRecord);
                    userRepository.updateSliverCoin(userId, signIn.getSliverPrice());
                } else {
                    throw new Exception("已經簽到");
                }

                return signIn;
            }
        }

        return null;
    }

}

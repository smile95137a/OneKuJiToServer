package com.one.frontend.service;

import com.one.frontend.model.UserTransaction;
import com.one.frontend.repository.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserTransactionRepository transactionRepository;

    public List<UserTransaction> getTransactions(Long userId, Date startDate, Date endDate) {
        List<UserTransaction> transactions;
        // 將 Date 轉換為 LocalDateTime
        LocalDateTime startLocalDateTime = convertToLocalDateTimeAtStartOfDay(startDate);
        LocalDateTime endLocalDateTime = convertToLocalDateTimeAtEndOfDay(endDate);

        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findTransactionsByUserIdAndDateRange(userId, startLocalDateTime, endLocalDateTime);
        } else {
            transactions = transactionRepository.findAllTransactionsByUserId(userId);
        }

        // 不需要再将中文设置回枚举
        // 通过 getFriendlyTransactionType() 在前端返回用户友好的字符串
        transactions.forEach(transaction -> {
            // 使用友好的类型（中文），返回给前端时处理为中文字符串
            System.out.println(transaction.getFriendlyTransactionType());
        });

        return transactions;
    }
    private static LocalDateTime convertToLocalDateTimeAtStartOfDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.atStartOfDay();  // 設置為該天的 00:00:00
    }

    // 將 Date 轉換為 LocalDateTime 並設置為該天的 23:59:59
    private static LocalDateTime convertToLocalDateTimeAtEndOfDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.atTime(LocalTime.MAX);  // 設置為該天的 23:59:59
    }
}

package com.one.controller;

import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.UserTransaction;
import com.one.frontend.request.OrderQueryReq;
import com.one.frontend.service.TransactionService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/getTransactions")
    public ResponseEntity<ApiResponse<List<UserTransaction>>> getTransactions(
           @RequestBody OrderQueryReq orderQueryReq) {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        List<UserTransaction> transactions = transactionService.getTransactions(userId, orderQueryReq.getStartDate(), orderQueryReq.getEndDate());
        ApiResponse<List<UserTransaction>> response = ResponseUtils.success(200 , null , transactions);
        return ResponseEntity.ok(response);
    }
}

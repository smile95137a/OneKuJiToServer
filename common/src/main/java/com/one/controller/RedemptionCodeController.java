package com.one.controller;

import com.one.frontend.service.RedemptionCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redemption")
public class RedemptionCodeController {

    @Autowired
    private RedemptionCodeService redemptionCodeService;

}

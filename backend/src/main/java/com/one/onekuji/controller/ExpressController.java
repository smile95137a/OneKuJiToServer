package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.Address;
import com.one.onekuji.request.CallHome;
import com.one.onekuji.request.HomeReq;
import com.one.onekuji.request.LogisticsRequest;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/express")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    @PostMapping("/convenience")
    public ResponseEntity<ApiResponse<?>> convenience(@RequestBody LogisticsRequest logisticsRequest) {
        try {
            String convenience = expressService.convenience(logisticsRequest);
            ApiResponse<?> response = ResponseUtils.success(200, null, convenience );
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @PostMapping("/homeAndOffice")
    public ResponseEntity<ApiResponse<?>> home(@RequestBody HomeReq homeReq) {
        try {
        String home = expressService.home(homeReq);
            ApiResponse<?> response = ResponseUtils.success(200, null, home );
        return ResponseEntity.ok(response);
    }catch (Exception e){
        e.printStackTrace();
    }
        return null;
    }

    @PostMapping("/getAddress")
    public ResponseEntity<ApiResponse<?>> getAddress(@RequestBody Address address) {
        String address1 = expressService.getAddress(address);
        ApiResponse<?> response = ResponseUtils.success(200, null, address1 );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callHome")
    public ResponseEntity<ApiResponse<?>> callHome(@RequestBody CallHome callHome) {
        String address1 = expressService.callHome(callHome);
        ApiResponse<?> response = ResponseUtils.success(200, null, address1);
        return ResponseEntity.ok(response);
    }

}

package com.one.onekuji.controller;

import com.one.onekuji.request.Address;
import com.one.onekuji.request.CallHome;
import com.one.onekuji.request.HomeReq;
import com.one.onekuji.request.LogisticsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/express")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    @PostMapping("/convenience")
    public ResponseEntity<String> convenience(@RequestBody LogisticsRequest logisticsRequest) {
        try {
            String convenience = expressService.convenience(logisticsRequest);
            return ResponseEntity.ok(convenience);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @PostMapping("/homeAndOffice")
    public ResponseEntity<String> home(@RequestBody HomeReq homeReq) {
        try {
        String home = expressService.home(homeReq);
        return ResponseEntity.ok(home);
    }catch (Exception e){
        e.printStackTrace();
    }
        return null;
    }

    @PostMapping("/getAddress")
    public ResponseEntity<String> getAddress(@RequestBody Address address) {
        String address1 = expressService.getAddress(address);
        return ResponseEntity.ok(address1);
    }

    @PostMapping("/callHome")
    public ResponseEntity<String> callHome(@RequestBody CallHome callHome) {
        String address1 = expressService.callHome(callHome);
        return ResponseEntity.ok(address1);
    }

}

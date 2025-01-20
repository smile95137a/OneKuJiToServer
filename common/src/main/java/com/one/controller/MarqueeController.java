package com.one.controller;

import com.one.service.MarqueeService;
import com.one.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/marquee")
@RequiredArgsConstructor
public class MarqueeController {

    private final MarqueeService marqueeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMarqueeWithDetailsAndUser() {
        try {
            var marquees = marqueeService.getAllMarqueeWithDetailsGroupedByMarqueeId();
            var response = ResponseUtils.success(200, "Fetched marquees successfully", marquees);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to fetch marquees", e);
            var response = ResponseUtils.failure(500, "Failed to fetch marquees", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

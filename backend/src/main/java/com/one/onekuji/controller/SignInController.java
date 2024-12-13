package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.SignIn;
import com.one.onekuji.service.SignInService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sign")
public class SignInController {

    @Autowired
    private SignInService signInService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SignIn>>> getAllSignIns() {
        List<SignIn> signInList = signInService.getAllSignIns();
        ApiResponse<List<SignIn>> response = ResponseUtils.success(200, null, signInList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SignIn>> getSignInById(@PathVariable("id") Long id) {
        SignIn signIn = signInService.getSignInById(id);
        if (signIn == null) {
            ApiResponse<SignIn> response = ResponseUtils.failure(404, "SignIn not found", null);
            return ResponseEntity.ok(response);
        }
        ApiResponse<SignIn> response = ResponseUtils.success(200, null, signIn);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SignIn>> createSignIn(@RequestBody SignIn signIn) {
        try {
            signInService.createSignIn(signIn);
            ApiResponse<SignIn> response = ResponseUtils.success(201, "SignIn created successfully", signIn);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<SignIn> response = ResponseUtils.failure(400, e.getMessage(), null);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SignIn>> updateSignIn(@PathVariable("id") Long id, @RequestBody SignIn signIn) {
        try {
            signInService.updateSignIn(id, signIn);
            ApiResponse<SignIn> response = ResponseUtils.success(200, "SignIn updated successfully", signIn);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<SignIn> response = ResponseUtils.failure(400, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSignIn(@PathVariable("id") Long id) {
        signInService.deleteSignIn(id);
        ApiResponse<Void> response = ResponseUtils.success(200, "SignIn deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}

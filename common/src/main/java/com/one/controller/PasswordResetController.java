package com.one.controller;

import com.one.exception.TokenVerificationException;
import com.one.request.GenResetPwdReq;
import com.one.request.ResetPwdReq;
import com.one.service.PasswordResetService;
import com.one.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

 private final PasswordResetService passwordResetService;

 @PostMapping("/genResetPwd")
 public ResponseEntity<?> genResetPwd(@RequestBody GenResetPwdReq req) {
  try {
   passwordResetService.createPasswordResetToken(req.getEmail(), 60);

   var res = ResponseUtils.success(200 , null , true);
   return ResponseEntity.ok(res);
  } catch (Exception e) {
   var res = ResponseUtils.failure(9999, "系統錯誤", e.getMessage());
   return ResponseEntity.badRequest().body(res);
  }
 }

 @PostMapping("/verifyPasswordToken/{token}")
 public ResponseEntity<?> verifyPasswordToken(@PathVariable String token) {
  try {
   var result = passwordResetService.verifyToken(token);
   var res = ResponseUtils.success(0000, null, result);
   return ResponseEntity.ok(res);
  } catch (TokenVerificationException e) {
   var res = ResponseUtils.failure(e.getCode(), "系統錯誤", e.getMessage());
   return ResponseEntity.badRequest().body(res);
  }
 }

 @PostMapping("/resetPassword")
 public ResponseEntity<?> resetPassword(@RequestBody ResetPwdReq req) {

  try {
   var result = passwordResetService.markPasswordAsChanged(req.getToken(), req.getPwd());
   var res = ResponseUtils.success(0000, null, result);
   return ResponseEntity.ok(res);
  } catch (Exception e) {
   var res = ResponseUtils.failure(9999, "系統錯誤", e.getMessage());
   return ResponseEntity.badRequest().body(res);
  }

 }
}
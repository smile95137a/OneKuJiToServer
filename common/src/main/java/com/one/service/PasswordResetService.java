package com.one.service;

import com.one.dto.PasswordResetRepo;
import com.one.exception.TokenVerificationException;
import com.one.repository.PasswordResetTokenMapper;
import com.one.repository.UserRepository;
import com.one.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

 private final UserRepository userRepository;

 private final PasswordResetRepo passwordResetTokenRepository;

 private final PasswordEncoder passwordEncoder;

 private final PasswordResetTokenMapper passwordResetTokenMapper;

 private final MailService mailService;

 @Value("${domain}")
 private String frontendDomain;

 @Transactional
 public void createPasswordResetToken(String email, int expiryMinutes) throws Exception {
  var userOptional = userRepository.findByUsername(email);
  if (userOptional.isEmpty()) {
   throw new Exception("找不到使用者");
  }

  var user = userOptional.get();
  var token = RandomUtils.genRandom(64, false);
  var prt = passwordResetTokenMapper.toPasswordResetToken(user.getId(), token, expiryMinutes);

  passwordResetTokenRepository.save(prt);
  var link = String.format("%s/restPwd/%s", frontendDomain, token);
  mailService.sendPassWordMail(user.getUsername(), link);
 }

 @Transactional
 public boolean verifyToken(String token) throws TokenVerificationException {
  var optionalPRT = passwordResetTokenRepository.findByToken(token);
  if (optionalPRT.isPresent()) {
   var prtEntity = optionalPRT.get();
   var userId = prtEntity.getUserId();

   var tokensEntity = passwordResetTokenRepository.findByUserIdOrderByCreateTimeDesc(userId);
   var lastTokenEntity = tokensEntity.get(0);

   if (prtEntity.getIsActive()) {
    throw new TokenVerificationException(9001, "連結已被使用，請您再確認。");
   }

   if (LocalDateTime.now().isAfter(prtEntity.getExpireTime())) {
    throw new TokenVerificationException(9002, "連結已過期，請您再確認。");
   }

   if (!StringUtils.equals(token, lastTokenEntity.getToken())) {
    throw new TokenVerificationException(9003, "無效的連結，請您再確認。");
   }

   prtEntity.setIsActive(true);
   prtEntity.setUpdateTime(LocalDateTime.now());
   passwordResetTokenRepository.save(prtEntity);
   return true;
  }
  throw new TokenVerificationException(9004, "不存在此連結，請您再確認。");
 }

 @Transactional
 public boolean markPasswordAsChanged(String token, String password) throws Exception {
  var optionalPRT = passwordResetTokenRepository.findByToken(token);
  if (optionalPRT.isPresent()) {
   var now = LocalDateTime.now();
   var prtEntity = optionalPRT.get();
   if (now.isAfter(prtEntity.getExpireTime())) {
    throw new Exception("token已過期。");
   }

   var userId = prtEntity.getUserId();
   var userEntity = userRepository.getUserBId(userId);
   var pwd = passwordEncoder.encode(password);
   userEntity.setId(userId);
   userEntity.setPassword(pwd);
   userEntity.setUpdatedAt(now);
   userRepository.resetUser(userEntity);

   prtEntity.setPasswordChanged(true);
   prtEntity.setUpdateTime(now);
   passwordResetTokenRepository.save(prtEntity);
   return true;
  } else {
   throw new Exception("找不到token。");
  }
 }
}
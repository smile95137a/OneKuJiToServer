package com.one.service;

import com.one.config.security.CustomUserDetails;
import com.one.config.security.JwtTokenProvider;
import com.one.config.security.SecurityUtils;
import com.one.dto.LoginDto;
import com.one.dto.LoginResponse;
import com.one.exception.AllException;
import com.one.model.User;
import com.one.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public LoginResponse login(LoginDto loginDto) throws Exception {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			CustomUserDetails userDetail = SecurityUtils.getCurrentUserPrinciple();
			String token = jwtTokenProvider.generateToken(userDetail);

			User user = userRepository.getUserByUserName(loginDto.getUsername());

			// 自定义验证逻辑
			if (user.getRoleId() == 4) {
				throw new AllException.UnverifiedUserException("用戶無認證");
			} else if (user.getRoleId() == 5) {
				throw new AllException.BlacklistedUserException("黑名單用戶");
			}

			return new LoginResponse(token, user.getId(), user.getUsername(), user.getUserUid());

		} catch (AuthenticationException e) {
			throw new AllException.InvalidCredentialsException("帳號密碼錯誤");
		}
	}

}
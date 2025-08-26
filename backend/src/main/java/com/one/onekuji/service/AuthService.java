package com.one.onekuji.service;


import com.one.onekuji.config.security.CustomUserDetails;
import com.one.onekuji.dto.LoginDto;
import com.one.onekuji.dto.LoginResponse;
import com.one.onekuji.model.User;
import com.one.onekuji.repository.UserRepository;
import com.one.onekuji.util.JwtTokenProvider;
import com.one.onekuji.util.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;


    public AuthService(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginDto loginDto) throws Exception {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetail = SecurityUtils.getCurrentUserPrinciple();
        String token = jwtTokenProvider.generateToken(userDetail);
        User user = userRepository.getUserByUserName(loginDto.getUsername());
        if (!(user.getRoleId() == 1 || user.getRoleId() == 2)) {
            throw new Exception("不屬於管理者，無法登入");
        }
        return new LoginResponse(token, user.getId(), user.getUsername() , user.getRoleId().toString());
    }
}
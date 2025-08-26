//package com.one.onekuji.service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//import com.one.onekuji.model.User;
//import com.one.onekuji.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//	private final UserRepository userRepository;
//
//	@Override
//	public UserDetails loadUserByUsername(String username) {
//		User user = userRepository.getUserByUserName(username);
//		List<SimpleGrantedAuthority> authorities = (user.getRoles() != null && !user.getRoles().isEmpty())
//			    ? user.getRoles().stream()
//			        .map(role -> new SimpleGrantedAuthority(role.getName()))
//			        .collect(Collectors.toList())
//			    : List.of();
//
//
// 		return mapUserToCustomUserDetails(user, authorities);
//	}
//
//	private CustomUserDetails mapUserToCustomUserDetails(User user, List<SimpleGrantedAuthority> authorities) {
//
//		return CustomUserDetails.builder()
//		.id(Long.valueOf(user.getId()))
//		.username(user.getUsername())
//		.password(user.getPassword())
//		.name(user.getNickname())
//		.email(user.getEmail())
//		.authorities(authorities)
//		.build();
//	}
//}
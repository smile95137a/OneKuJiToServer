//package com.one.frontend.config.security;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.one.frontend.model.User;
//import com.one.frontend.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//	private final UserRepository userRepository;
//
//	@Override
//	public UserDetails loadUserByUsername(String username) {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
//		List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
//			    .map(role -> new SimpleGrantedAuthority(role.getName()))
//			    .collect(Collectors.toList());
//
//		return mapUserToCustomUserDetails(user, authorities);
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
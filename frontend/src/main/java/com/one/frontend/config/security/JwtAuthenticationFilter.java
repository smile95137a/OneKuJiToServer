package com.one.frontend.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
	private static final String BEARER_TOKEN_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader(HEADER_AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String jwtToken = getJwtToken(request);
			String username = jwtTokenProvider.extractUsername(jwtToken);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (username != null && authentication == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtTokenProvider.isTokenValid(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication -> Message: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String getJwtToken(HttpServletRequest request) {
		String header = request.getHeader(HEADER_AUTHORIZATION);

		if (header != null && header.startsWith(BEARER_TOKEN_PREFIX)) {
			return header.substring(7);
		}

		return null;
	}
}

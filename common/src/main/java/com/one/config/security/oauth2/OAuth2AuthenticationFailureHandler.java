package com.one.config.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Value("${app.oauth.redirectUrl}")
	private String redirectUrl;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		exception.printStackTrace();

		String errorMessage = URLEncoder.encode(String.format("oauth登錄失敗，原因: %s", exception.getMessage()),
				StandardCharsets.UTF_8.toString());
		String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl).queryParam("errorMsg", errorMessage).build()
				.toUriString();
		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}
}

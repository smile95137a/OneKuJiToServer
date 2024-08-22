package com.one.frontend.config.security.oauth2;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    	 exception.printStackTrace();
    	 
         String targetUrl = UriComponentsBuilder.fromUriString("/")
                 .queryParam("error", exception.getLocalizedMessage())
                 .build().toUriString();
         
         getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

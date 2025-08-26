package com.one.frontend.config.security.oauth2;

import java.util.Map;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class OAuth2Utils {
    public static CustomAbstractOAuth2UserInfo getOAuth2UserInfo(String registrationId,
            Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(OAuth2Provider.GOOGLE.toString())) {
            return new GoogleCustomAbstractOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(OAuth2Provider.FACEBOOK.toString())) {
            return new FacebookCustomAbstractOAuth2UserInfo(attributes);
        } else {
            throw new InternalAuthenticationServiceException(
                    "抱歉！目前尚不支持使用 " + registrationId + " 登錄。");
        }
    }
}

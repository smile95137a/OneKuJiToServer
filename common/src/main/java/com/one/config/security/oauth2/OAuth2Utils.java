package com.one.config.security.oauth2;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.util.Map;

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

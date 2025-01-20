package com.one.config.security.oauth2;

public enum OAuth2Provider {
    GOOGLE,
    FACEBOOK,
    GITHUB;

    public static OAuth2Provider fromString(String providerName) {
        try {
            return OAuth2Provider.valueOf(providerName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid OAuth2 provider: " + providerName);
        }
    }
}

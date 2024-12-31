package com.one.exception;

public class AllException {
    // 账号密码错误异常
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    // 用户没有认证异常
    public static class UnverifiedUserException extends RuntimeException {
        public UnverifiedUserException(String message) {
            super(message);
        }
    }

    // 黑名单用户异常
    public static class BlacklistedUserException extends RuntimeException {
        public BlacklistedUserException(String message) {
            super(message);
        }
    }
}

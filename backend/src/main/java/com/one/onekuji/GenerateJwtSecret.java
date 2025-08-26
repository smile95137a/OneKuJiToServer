package com.one.onekuji;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        // 生成一个新的密钥
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        // 输出 Base64 编码的密钥
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Base64 Encoded Key: " + base64Key);
    }
}

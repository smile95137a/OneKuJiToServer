package com.one.onekuji.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ImageUtils {

    public static String imageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath); // 使用 File 类从文件系统中读取文件
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }
}

package com.one.onekuji.util;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ImageUtil {

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePathMapping;

    private static String staticPicturePath;
    private static String staticPicturePathMapping;
    private static final int TARGET_SIZE = 400;
    private static final float OUTPUT_QUALITY = 0.85f;

    @PostConstruct
    public void init() {
        staticPicturePath = picturePath.endsWith("/") ? picturePath : picturePath + "/";
        staticPicturePathMapping = picturePathMapping.endsWith("/") ? picturePathMapping : picturePathMapping + "/";
    }

    public static String upload(MultipartFile file) {
        return uploadFile(file, false);
    }

    public static String uploadForCKEditor(MultipartFile file) {
        return uploadFile(file, true);
    }

    private static String uploadFile(MultipartFile file, boolean isForCKEditor) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String filePath = staticPicturePath + uniqueFileName;
        File dest = new File(filePath);

        try {
            Files.createDirectories(Paths.get(staticPicturePath));

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IOException("Failed to read image file");
            }

            if (isForCKEditor) {
                Thumbnails.of(originalImage)
                        .scale(1)
                        .toFile(dest);
            } else {
                BufferedImage processedImage = processImageWithAspectRatio(originalImage);
                ImageIO.write(processedImage, getFileExtension(originalFileName), dest);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed", e);
        }

        return staticPicturePathMapping + uniqueFileName;
    }

    private static BufferedImage processImageWithAspectRatio(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 計算縮放比例
        double scale = Math.min(
                (double) TARGET_SIZE / originalWidth,
                (double) TARGET_SIZE / originalHeight
        );

        // 計算縮放後的尺寸
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        // 創建一個 400x400 的白色背景圖片
        BufferedImage finalImage = new BufferedImage(
                TARGET_SIZE,
                TARGET_SIZE,
                BufferedImage.TYPE_INT_ARGB
        );

        // 獲取繪圖上下文並設置白色背景
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, TARGET_SIZE, TARGET_SIZE);

        try {
            // 縮放原圖
            BufferedImage scaledImage = Thumbnails.of(originalImage)
                    .size(scaledWidth, scaledHeight)
                    .outputQuality(OUTPUT_QUALITY)
                    .asBufferedImage();

            // 計算居中位置
            int x = (TARGET_SIZE - scaledWidth) / 2;
            int y = (TARGET_SIZE - scaledHeight) / 2;

            // 在白色背景上繪製縮放後的圖片
            g2d.drawImage(scaledImage, x, y, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        } finally {
            g2d.dispose();
        }

        return finalImage;
    }

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
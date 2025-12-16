package com.one.onekuji.util.storage;

import com.one.onekuji.config.S3Properties;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "pictureFile.storage-type", havingValue = "s3")
public class S3ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    private static final int TARGET_SIZE = 400;
    private static final int RECT_WIDTH = 600;
    private static final int RECT_HEIGHT = 300;
    private static final float OUTPUT_QUALITY = 0.85f;

    public S3ImageStorageService(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
        if (this.s3Properties == null || this.s3Properties.getBucketName() == null || this.s3Properties.getBucketName().isEmpty()) {
            throw new IllegalStateException("S3 bucket name is not configured. Please set s3.bucket-name property.");
        }
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        return uploadInternal(file.getInputStream(), file.getOriginalFilename(), false);
    }

    @Override
    public String uploadForCKEditor(MultipartFile file) throws IOException {
        return uploadInternal(file.getInputStream(), file.getOriginalFilename(), true);
    }

    @Override
    public String uploadRectangle(MultipartFile file) throws IOException {
        return uploadRectangleInternal(file.getInputStream(), file.getOriginalFilename());
    }

    @Override
    public String upload(InputStream inputStream, String originalFilename, String contentType) throws IOException {
        return uploadInternal(inputStream, originalFilename, false);
    }

    private String uploadInternal(InputStream inputStream, String originalFilename, boolean isForCKEditor) throws IOException {
        if (originalFilename == null) {
            originalFilename = "image";
        }
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension == null) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Failed to read image file");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (isForCKEditor) {
            Thumbnails.of(originalImage).scale(1).outputFormat(fileExtension).toOutputStream(baos);
        } else {
            BufferedImage processedImage = processImageWithAspectRatio(originalImage);
            ImageIO.write(processedImage, fileExtension, baos);
        }

        byte[] bytes = baos.toByteArray();
        String key = "images/" + uniqueFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType("image/" + fileExtension)
                // 移除 ACL 設定，改用 Bucket Policy 控制公開存取
                // .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        // 上傳到 S3 的 images/ 目錄，但只回傳 /檔名
        // 前端組合：https://onemorelottery.tw/images + /檔名
        return "/" + uniqueFileName;
    }

    private String uploadRectangleInternal(InputStream inputStream, String originalFilename) throws IOException {
        if (originalFilename == null) {
            originalFilename = "rectangle_image";
        }
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension == null) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Failed to read image file");
        }

        BufferedImage processedImage = processRectangleImageWithAspectRatio(originalImage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, fileExtension, baos);
        byte[] bytes = baos.toByteArray();
        String key = "images/" + uniqueFileName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType("image/" + fileExtension)
                // 移除 ACL 設定，改用 Bucket Policy 控制公開存取
                // .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        // 上傳到 S3 的 images/ 目錄，但只回傳 /檔名
        // 前端組合：https://onemorelottery.tw/images + /檔名
        return "/" + uniqueFileName;
    }

    private BufferedImage processImageWithAspectRatio(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double scale = Math.min((double) TARGET_SIZE / originalWidth, (double) TARGET_SIZE / originalHeight);
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);
        BufferedImage finalImage = new BufferedImage(TARGET_SIZE, TARGET_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, TARGET_SIZE, TARGET_SIZE);
        try {
            BufferedImage scaledImage = Thumbnails.of(originalImage).size(scaledWidth, scaledHeight).outputQuality(OUTPUT_QUALITY).asBufferedImage();
            int x = (TARGET_SIZE - scaledWidth) / 2;
            int y = (TARGET_SIZE - scaledHeight) / 2;
            g2d.drawImage(scaledImage, x, y, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        } finally {
            g2d.dispose();
        }
        return finalImage;
    }

    private BufferedImage processRectangleImageWithAspectRatio(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double scale = Math.min((double) RECT_WIDTH / originalWidth, (double) RECT_HEIGHT / originalHeight);
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);
        BufferedImage finalImage = new BufferedImage(RECT_WIDTH, RECT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, RECT_WIDTH, RECT_HEIGHT);
        try {
            BufferedImage scaledImage = Thumbnails.of(originalImage).size(scaledWidth, scaledHeight).outputQuality(OUTPUT_QUALITY).asBufferedImage();
            int x = (RECT_WIDTH - scaledWidth) / 2;
            int y = (RECT_HEIGHT - scaledHeight) / 2;
            g2d.drawImage(scaledImage, x, y, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process rectangle image", e);
        } finally {
            g2d.dispose();
        }
        return finalImage;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return null;
    }
}

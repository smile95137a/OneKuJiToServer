package com.one.onekuji.util.storage;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "pictureFile.storage-type", havingValue = "local", matchIfMissing = true)
public class LocalImageStorageService implements ImageStorageService {

    @Value("${pictureFile.path:uploads/}")
    private String picturePath;

    @Value("${pictureFile.path-mapping:/uploads/}")
    private String picturePathMapping;

    private static final int TARGET_SIZE = 400;
    private static final int RECT_WIDTH = 600;
    private static final int RECT_HEIGHT = 300;
    private static final float OUTPUT_QUALITY = 0.85f;

    private String ensurePath() {
        return picturePath.endsWith("/") ? picturePath : picturePath + "/";
    }

    private String ensurePathMapping() {
        return picturePathMapping.endsWith("/") ? picturePathMapping : picturePathMapping + "/";
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        return uploadFileInternal(file.getInputStream(), file.getOriginalFilename(), false);
    }

    @Override
    public String uploadForCKEditor(MultipartFile file) throws IOException {
        return uploadFileInternal(file.getInputStream(), file.getOriginalFilename(), true);
    }

    @Override
    public String uploadRectangle(MultipartFile file) throws IOException {
        return uploadRectangleInternal(file.getInputStream(), file.getOriginalFilename());
    }

    @Override
    public String upload(InputStream inputStream, String originalFilename, String contentType) throws IOException {
        // Default to non CKEditor processing
        return uploadFileInternal(inputStream, originalFilename, false);
    }

    private String uploadFileInternal(InputStream inputStream, String originalFilename, boolean isForCKEditor) throws IOException {
        if (originalFilename == null) {
            originalFilename = "image";
        }
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension == null) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        String pathDir = ensurePath();
        String filePath = pathDir + uniqueFileName;
        File dest = new File(filePath);

        Files.createDirectories(Paths.get(pathDir));

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Failed to read image file");
        }

        if (isForCKEditor) {
            Thumbnails.of(originalImage).scale(1).toFile(dest);
        } else {
            BufferedImage processedImage = processImageWithAspectRatio(originalImage);
            ImageIO.write(processedImage, fileExtension, dest);
        }

        return ensurePathMapping() + uniqueFileName;
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

        String pathDir = ensurePath();
        String filePath = pathDir + uniqueFileName;
        File dest = new File(filePath);

        Files.createDirectories(Paths.get(pathDir));

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Failed to read image file");
        }

        BufferedImage processedImage = processRectangleImageWithAspectRatio(originalImage);
        ImageIO.write(processedImage, fileExtension, dest);

        return ensurePathMapping() + uniqueFileName;
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

package com.one.onekuji.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Component
public class ImageUtil {

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePathMapping;

    private static String staticPicturePath;
    private static String staticPicturePathMapping;
    private static final int TARGET_SIZE = 1600;
    private static final int STORE_TARGET_SIZE = 2000;
    private static final int RECT_WIDTH = 800;
    private static final int STORE_TARGET_WIDTH = 380;
    private static final int RECT_HEIGHT = 600;
    private static final int STORE_TARGET_HEIGHT = 216;
    private static final float OUTPUT_QUALITY = 0.85f;
    private static final float STORE_OUTPUT_QUALITY = 0.85f;

    @PostConstruct
    public void init() {
        staticPicturePath = picturePath.endsWith("/") ? picturePath : picturePath + "/";
        staticPicturePathMapping = picturePathMapping.endsWith("/") ? picturePathMapping : picturePathMapping + "/";
    }

    public static String[] upload(MultipartFile file, int[][] rwdSizes) {
        return uploadFile(file, rwdSizes, false, false);
    }
    public static String[] upload(MultipartFile file, int[][] rwdSizes, boolean useCrop) {
        return uploadFile(file, rwdSizes, false, useCrop);
    }

    public static String uploadForCKEditor(MultipartFile file) {
        return uploadFile(file, null, true, false)[0];
    }

    private static String[] uploadFile(MultipartFile file, int[][] rwdSizes, boolean isForCKEditor, boolean useCrop) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_"
                + (originalFileName != null ? originalFileName : "image");
        String fileExtension = getFileExtension(originalFileName);

        if (fileExtension == null) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        var filePaths = new ArrayList<String>();
        String filePath = staticPicturePath + uniqueFileName;
        File dest = new File(filePath);

        try {
            Files.createDirectories(Paths.get(staticPicturePath));

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IOException("Failed to read image file");
            }

            if (isForCKEditor) {
                Thumbnails.of(originalImage).scale(1).toFile(dest);
                filePaths.add(staticPicturePathMapping + uniqueFileName);
            } else {
                for (int[] size : rwdSizes) {
                    String rwdFilePath = String.format("%s%dx%d_%s", staticPicturePath, size[0], size[1], uniqueFileName);
                    File rwdDest = new File(rwdFilePath);
                    BufferedImage processedImage = useCrop ? cropImageFromCenter(originalImage, size[0], size[1]) 
                                                            : processImageWithAspectRatio(originalImage, size[0], size[1]);
                    ImageIO.write(processedImage, fileExtension, rwdDest);
                    
                    String mappingFilePath = String.format("%s%dx%d_%s", staticPicturePathMapping, size[0], size[1], uniqueFileName);
                    filePaths.add(mappingFilePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed", e);
        }

        return filePaths.toArray(new String[0]);
    }

    private static BufferedImage cropImageFromCenter(BufferedImage image, double targetWidth, double targetHeight)
            throws IOException {
        return Thumbnails.of(image).size((int) targetWidth, (int) targetHeight).crop(Positions.TOP_CENTER)
                .asBufferedImage();
    }

    public static BufferedImage processImageWithAspectRatio(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double scale = Math.min((double) targetWidth / originalWidth, (double) targetHeight / originalHeight);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        BufferedImage finalImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, targetWidth, targetHeight);

        try {
            BufferedImage scaledImage = Thumbnails.of(originalImage)
                    .size(scaledWidth, scaledHeight)
                    .outputQuality(OUTPUT_QUALITY)
                    .asBufferedImage();

            int x = (targetWidth - scaledWidth) / 2; 
            int y = 0; 

            g2d.drawImage(scaledImage, x, y, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        } finally {
            g2d.dispose();
        }

        return finalImage;
    }



    private static String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return null;
    }
}

package com.one.onekuji.util;

import com.one.onekuji.util.storage.ImageStorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageUtil {

    private static ImageStorageService delegate;

    public ImageUtil(ImageStorageService imageStorageService) {
        ImageUtil.delegate = imageStorageService;
    }

    public static String upload(MultipartFile file) {
        if (delegate == null) {
            throw new IllegalStateException("ImageStorageService delegate is not initialized. Make sure the Spring context is up and configure pictureFile.storage-type property.");
        }
        try {
            return delegate.upload(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String uploadForCKEditor(MultipartFile file) {
        if (delegate == null) {
            throw new IllegalStateException("ImageStorageService delegate is not initialized. Make sure the Spring context is up and configure pictureFile.storage-type property.");
        }
        try {
            return delegate.uploadForCKEditor(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String uploadRectangle(MultipartFile file) {
        if (delegate == null) {
            throw new IllegalStateException("ImageStorageService delegate is not initialized. Make sure the Spring context is up and configure pictureFile.storage-type property.");
        }
        try {
            return delegate.uploadRectangle(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

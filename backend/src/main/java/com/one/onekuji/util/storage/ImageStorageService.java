package com.one.onekuji.util.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageService {
    String upload(MultipartFile file) throws IOException;
    String uploadForCKEditor(MultipartFile file) throws IOException;
    String uploadRectangle(MultipartFile file) throws IOException;
    // Helper upload for migration by stream and original filename
    String upload(InputStream inputStream, String originalFilename, String contentType) throws IOException;
}

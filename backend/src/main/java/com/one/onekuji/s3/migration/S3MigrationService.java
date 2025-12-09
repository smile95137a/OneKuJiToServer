package com.one.onekuji.s3.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.config.S3Properties;
import com.one.onekuji.util.storage.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
// stream.Collectors not used

@Service
public class S3MigrationService {

    private final JdbcTemplate jdbcTemplate;
    private final ImageStorageService imageStorageService; // should be S3 implementation at runtime
    private final ObjectMapper objectMapper;
    private final S3Properties s3Properties;

    @Value("${pictureFile.path-mapping:/uploads/}")
    private String picturePathMapping;

    @Value("${pictureFile.path:uploads/}")
    private String picturePath;

    public S3MigrationService(JdbcTemplate jdbcTemplate, ImageStorageService imageStorageService, ObjectMapper objectMapper, S3Properties s3Properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.imageStorageService = imageStorageService;
        this.objectMapper = objectMapper;
        this.s3Properties = s3Properties;
    }

    record TableInfo(@NonNull String table, @NonNull String idColumn, @NonNull String imageColumn, boolean jsonArray) {}

    @Value("${s3.migration.dry-run:true}")
    private boolean dryRun;

    @Value("${s3.migration.migrate-remote-urls:true}")
    private boolean migrateRemoteUrls;

    public void migrateAllTables() {
        List<TableInfo> tables = List.of(
            new TableInfo("store_product", "store_product_id", "image_urls", true),
            new TableInfo("product", "product_id", "image_urls", true),
            new TableInfo("product_detail", "product_detail_id", "image_urls", true),
            new TableInfo("news", "news_id", "image_urls", true),
            new TableInfo("draw_result", "draw_result_id", "image_urls", false),
            new TableInfo("banner", "banner_id", "banner_image_urls", true)
        );

        for (TableInfo t : tables) {
            migrateTable(t);
        }
    }

    private void migrateTable(TableInfo t) {
    String sql = String.format("SELECT %s as id, %s FROM %s WHERE %s IS NOT NULL AND %s <> ''",
        Objects.requireNonNull(t.idColumn()), Objects.requireNonNull(t.imageColumn()), Objects.requireNonNull(t.table()), Objects.requireNonNull(t.imageColumn()), Objects.requireNonNull(t.imageColumn()));
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(Objects.requireNonNull(sql));
        System.out.println("Migrating table: " + t.table() + ", rows found: " + rows.size());
        for (Map<String, Object> row : rows) {
            Object id = row.get("id");
            Object imageColObj = row.get(t.imageColumn());
            if (imageColObj == null) continue;
            String imageCol = imageColObj.toString();
            try {
                List<String> imageUrls;
                if (t.jsonArray()) {
                    imageUrls = objectMapper.readValue(imageCol, new TypeReference<>() {});
                } else {
                    // comma-separated
                    imageUrls = List.of(imageCol.split(","));
                }
                List<String> newUrls = new ArrayList<>();
                for (String url : imageUrls) {
                    url = url == null ? "" : url.trim();
                    if (url.isEmpty()) continue;
                    if (isAlreadyS3(url)) {
                        newUrls.add(url);
                        continue;
                    }
                    String newUrl = migrateSingleUrl(url);
                    if (newUrl != null) newUrls.add(newUrl);
                }
                String updatedValue;
                if (t.jsonArray()) {
                    updatedValue = objectMapper.writeValueAsString(newUrls);
                } else {
                    updatedValue = String.join(",", newUrls);
                }
                if (dryRun) {
                    System.out.println("[DRY-RUN] Skipping DB update for table=" + t.table() + " id=" + id + " newValue=" + updatedValue);
                } else {
                    String updateSql = String.format("UPDATE %s SET %s = ? WHERE %s=?", Objects.requireNonNull(t.table()), Objects.requireNonNull(t.imageColumn()), Objects.requireNonNull(t.idColumn()));
                    jdbcTemplate.update(Objects.requireNonNull(updateSql), updatedValue, id);
                    System.out.println("Updated row id=" + id + " for table=" + t.table());
                }
            } catch (Exception e) {
                System.err.println("Failed to migrate row id=" + id + " for table=" + t.table() + ", err=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean isAlreadyS3(String url) {
        if (url == null || url.isEmpty()) return false;
    if (s3Properties != null && s3Properties.getBucketName() != null && !s3Properties.getBucketName().isEmpty() && url.contains(s3Properties.getBucketName())) return true;
        if (url.contains("s3.amazonaws.com")) return true;
        if (url.contains("amazonaws.com")) return true;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            // we consider not S3 if URL doesn't belong to S3 - return false here
            return false;
        }
        return false;
    }

    private String migrateSingleUrl(String url) {
        try {
            if (picturePathMapping != null && url.contains(picturePathMapping)) {
                String fileName = url.substring(url.indexOf(picturePathMapping) + picturePathMapping.length());
                File file = new File(picturePath + (picturePath.endsWith("/") ? "" : "/") + fileName);
                if (file.exists()) {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String newUrl = imageStorageService.upload(new ByteArrayInputStream(bytes), file.getName(), Files.probeContentType(file.toPath()));
                    return newUrl;
                } else {
                    System.err.println("Local file not found for url: " + url + ", path=" + file.getAbsolutePath());
                }
            }

            if (url.startsWith("http://") || url.startsWith("https://")) {
                if (!migrateRemoteUrls) {
                    System.out.println("Skipping remote url: " + url + " because s3.migration.migrate-remote-urls=false");
                    return null;
                }
                // download
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() == 200) {
                    String filename = extractFilenameFromUrl(url);
                    String contentType = response.headers().firstValue("Content-Type").orElse("image/jpeg");
                    String newUrl = imageStorageService.upload(new ByteArrayInputStream(response.body()), filename, contentType);
                    return newUrl;
                } else {
                    System.err.println("Failed to download: " + url + " status:" + response.statusCode());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractFilenameFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                fileName = "image_" + System.currentTimeMillis() + ".jpg";
            }
            return fileName;
        } catch (Exception e) {
            return "image_" + System.currentTimeMillis() + ".jpg";
        }
    }
}

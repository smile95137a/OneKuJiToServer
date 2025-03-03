package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.News;
import com.one.onekuji.service.NewsService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;
import com.one.onekuji.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<News>>> getAllNews() {
        try {
            List<News> newsList = newsService.getAllNews();
            ApiResponse<List<News>> response = ResponseUtils.success(200, null, newsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<News>> response = ResponseUtils.failure(500, "获取新闻列表失败", null);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{newsUid}")
    public ResponseEntity<ApiResponse<News>> getNewsById(@PathVariable String newsUid) {
        try {
//            CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
            News news = newsService.getNewsById(newsUid);
            if (news != null) {
                ApiResponse<News> response = ResponseUtils.success(200, null, news);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<News> response = ResponseUtils.failure(404, "新闻不存在", null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<News> response = ResponseUtils.failure(500, "获取新闻失败", null);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createNews(@RequestPart("newsReq") String news,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            // 将传入的 newsReq 转换为 News 对象
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            News newsReq = objectMapper.readValue(news, News.class);

            // 上传图片，并替换 newsReq 中的 blob 图片路径
            List<String> fileUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // 上传图片并获取 URL
                        String fileUrl = ImageUtil.uploadForCKEditor(image); // 确保上传后生成完整的 URL
                        fileUrls.add(fileUrl);
                    }
                }
            }

            // 替换 newsReq 中的 blob URL 为上传后的 URL
            String contentWithUpdatedUrls = replaceBlobUrlsWithFileUrls(newsReq.getContent(), fileUrls);
            newsReq.setContent(contentWithUpdatedUrls);

            // 将图片 URL 设置到 News 对象中
            newsReq.setImageUrls(fileUrls);

            // 保存新闻数据
            int result = newsService.insertNews(newsReq);
            if (result > 0) {
                ApiResponse<Void> response = ResponseUtils.success(201, "新闻创建成功", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Void> response = ResponseUtils.failure(400, "新闻创建失败", null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Void> response = ResponseUtils.failure(500, "创建新闻时发生错误", null);
            return ResponseEntity.ok(response);
        }
    }

    private String replaceBlobUrlsWithFileUrls(String content, List<String> fileUrls) {
        // 假设 images 是按照顺序上传并生成 URL 的，你可以遍历所有 blob 并替换为实际的图片 URL
        for (String fileUrl : fileUrls) {
            // 替换匹配的 blob URL
            content = content.replaceFirst("blob:[^\\s\"]+", fileUrl);
        }
        return content;
    }

    @PostMapping("/img/upload")
    public ResponseEntity<ApiResponse<String>> updateNews(@RequestParam(value="file") MultipartFile file){
		int[][] rwdSizes = { 
				{ 1920, 1080 },
				{ 1280, 720 }, 
				{ 1024, 768 },
				{ 750, 750 },
		};
        String[] upload = ImageUtil.upload(file, rwdSizes);
        ApiResponse<String> response = ResponseUtils.success(200, "upload", upload[0]);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{newsUid}")
    public ResponseEntity<ApiResponse<Void>> updateNews(@PathVariable String newsUid,
                                                        @RequestPart("newsReq") String news,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {
        try {
            // 将传入的 newsReq 转换为 News 对象
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            News newsReq = objectMapper.readValue(news, News.class);

            // 上传图片，并获取对应的文件 URL 列表
            List<String> fileUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // 上传图片并获取 URL
                        String fileUrl = ImageUtil.uploadForCKEditor(image);  // 适用于 CKEditor 的上传方法
                        fileUrls.add(fileUrl);
                    }
                }
            }else{
                News storeProductRes = newsService.getNewsById(newsUid);
                List<String> list = storeProductRes.getImageUrls();
                if(list.isEmpty()){
                    list = new ArrayList<>();
                }
                fileUrls.addAll(list);
            }

            // 替换内容中的 Blob URL 为实际上传的图片 URL
            String updatedContent = replaceBlobUrlsWithFileUrls2(newsReq.getContent(), fileUrls);
            newsReq.setContent(updatedContent);

            // 更新图片 URL 列表到 News 对象中（如果有此字段）
            newsReq.setImageUrls(fileUrls);

            // 调用 service 层更新新闻
            int result = newsService.updateNews(newsUid, newsReq);

            // 返回成功响应
                ApiResponse<Void> response = ResponseUtils.success(200, "新闻更新成功", null);
                return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Void> response = ResponseUtils.failure(500, "更新新闻时发生错误", null);
            return ResponseEntity.ok(response);
        }
    }

    private String replaceBlobUrlsWithFileUrls2(String content, List<String> fileUrls) {
        // 假设这里有一个方法可以提取 Blob URL，暂时用一个示例的方式
        List<String> blobUrls = extractBlobUrls(content);

        for (int i = 0; i < blobUrls.size(); i++) {
            String blobUrl = blobUrls.get(i);
            // 替换 Blob URL 为实际文件 URL
            if (i < fileUrls.size()) {
                content = content.replace(blobUrl, fileUrls.get(i));
            }
        }
        return content;
    }

    public static List<String> extractBlobUrls(String content) {
        List<String> blobUrls = new ArrayList<>();
        // 正则表达式匹配 Blob URL
        String blobUrlRegex = "blob:[^\"']+";
        Pattern pattern = Pattern.compile(blobUrlRegex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            blobUrls.add(matcher.group());
        }
        return blobUrls;
    }

    @DeleteMapping("/{newsUid}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable String newsUid) {
        try {
            // 获取当前用户ID
            var userDetails = SecurityUtils.getCurrentUserPrinciple();
            int result = newsService.deleteNews(newsUid);
            if (result > 0) {
                ApiResponse<Void> response = ResponseUtils.success(200, "新闻删除成功", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Void> response = ResponseUtils.failure(400, "新闻删除失败", null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            ApiResponse<Void> response = ResponseUtils.failure(500, "删除新闻时发生错误", null);
            return ResponseEntity.ok(response);
        }
    }
}

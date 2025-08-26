package com.one.onekuji.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class HtmlProcessor {

    private static final String BASE_URL = "https://api.onemorelottery.tw:8080/img/";

    public static String processHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        Document doc = Jsoup.parse(html);
        Elements images = doc.select("img");

        for (Element img : images) {
            String src = img.attr("src");

            if (src.startsWith("data:image")) {
                // 處理 base64 圖片
                try {
                    // 將 base64 轉換為 MultipartFile
                    String[] parts = src.split(",");
                    String imageType = parts[0].split(";")[0].split("/")[1];
                    byte[] imageBytes = Base64.getDecoder().decode(parts[1]);

                    MultipartFile multipartFile = new MockMultipartFile(
                            "image." + imageType,
                            "image." + imageType,
                            "image/" + imageType,
                            imageBytes
                    );

                    // 使用 ImageUtil 保存圖片
                    String newUrl = ImageUtil.uploadForCKEditor(multipartFile);
                    img.attr("src", BASE_URL + newUrl.substring(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 處理相對路徑
            else if (src.startsWith("/")) {
                img.attr("src", BASE_URL + src.substring(1));
            }
        }

        return doc.body().html();
    }
}
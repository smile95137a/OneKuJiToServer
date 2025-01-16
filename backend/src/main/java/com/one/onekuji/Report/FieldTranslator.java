package com.one.onekuji.Report;

import java.util.HashMap;
import java.util.Map;

public class FieldTranslator {
    private static final Map<String, String> fieldMapping = new HashMap<>();

    static {
        fieldMapping.put("time_group", "日期");
        fieldMapping.put("year_group", "年");
        fieldMapping.put("gold_amount", "金幣");
        fieldMapping.put("silver_amount", "銀幣");
        fieldMapping.put("bonus_amount", "紅利");
        fieldMapping.put("other_amount", "其他金額");
        fieldMapping.put("total_amount", "總金額");
        fieldMapping.put("product_name", "產品名稱");
        fieldMapping.put("image_urls", "圖片");
        fieldMapping.put("grade", "等級");
        fieldMapping.put("nickname", "暱稱");
        fieldMapping.put("sliver_coin", "銀幣");
        fieldMapping.put("bonus", "紅利");
        fieldMapping.put("reward_points", "每日簽到銀幣");
        fieldMapping.put("group_type" , "日期分組");
        fieldMapping.put("day" , "日報");
        fieldMapping.put("week" , "週報");
        fieldMapping.put("month" , "月報");
        fieldMapping.put("year" , "年報");
        fieldMapping.put("product_detail_name" , "賞品名稱");
        fieldMapping.put("total_sliver_coin" , "銀幣總額");
        fieldMapping.put("p_product_name" , "產品名稱");
        fieldMapping.put("total_bonus" , "紅利總額");
        fieldMapping.put("phone_number" , "電話號碼");
        fieldMapping.put("amount_with_type" , "金幣與幣種");
        fieldMapping.put("user_email" , "信箱帳號");
        fieldMapping.put("user_nickname" , "暱稱");
        fieldMapping.put("address_name" , "姓名");
        fieldMapping.put("user_id" , "會員編號");
        fieldMapping.put("grade" , "賞品等級");
    }

    public static String translate(String fieldName) {
        return fieldMapping.getOrDefault(fieldName, fieldName); // 若無對應的中文，則回傳原字段名稱
    }
}

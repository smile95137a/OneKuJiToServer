package com.one.onekuji.Report;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 通用的報表查詢接口
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getReport(
            @RequestParam("reportType") String reportType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam("groupType") String groupType) {

        try {
            LocalDateTime start;
            LocalDateTime end = LocalDateTime.now(); // 默认结束时间为当前时间

            // 如果提供了日期参数，尝试解析；否则按 groupType 推算
            if (startDate != null && !startDate.isEmpty()) {
                // 判断日期格式
                if (startDate.contains("T")) {
                    // 如果日期包含时间部分，直接解析为 LocalDateTime
                    start = LocalDateTime.parse(startDate);
                } else {
                    // 如果只有日期部分，补充时间为 00:00:00
                    start = LocalDate.parse(startDate).atStartOfDay();
                }
            } else {
                // 如果前端未传，根据 groupType 推算
                switch (groupType.toLowerCase()) {
                    case "day":
                        start = end.toLocalDate().atStartOfDay(); // 设置为今天零点
                        break;
                    case "week":
                        start = end.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(); // 当周的周一零点
                        break;
                    case "month":
                        start = end.withDayOfMonth(1).toLocalDate().atStartOfDay(); // 当月的第一天零点
                        break;
                    case "year":
                        start = end.withDayOfYear(1).toLocalDate().atStartOfDay(); // 当年的第一天零点
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid groupType: " + groupType);
                }
            }

            // 如果未传 endDate，默认为当前时间
            if (endDate != null && !endDate.isEmpty()) {
                // 判断日期格式
                if (endDate.contains("T")) {
                    end = LocalDateTime.parse(endDate);
                } else {
                    end = LocalDate.parse(endDate).atStartOfDay();
                }
            } else {
                end = LocalDateTime.now();
            }

            // 调用服务层，处理时间范围
            List<Map<String, Object>> reportData = reportService.getReportData(reportType, start, end, groupType);

            // 使用 FieldTranslator 将字段名转换为繁体中文
            List<Map<String, Object>> translatedData = new ArrayList<>();
            for (Map<String, Object> dataRow : reportData) {
                Map<String, Object> translatedRow = new HashMap<>();
                for (Map.Entry<String, Object> entry : dataRow.entrySet()) {
                    String translatedField = FieldTranslator.translate(entry.getKey()); // 轉換字段名稱
                    translatedRow.put(translatedField, entry.getValue()); // 將轉換後的字段名稱與對應的值放入新結果
                }
                translatedData.add(translatedRow);
            }

            return ResponseEntity.ok(translatedData);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error occurred while fetching report: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportReport(
            @RequestParam("reportType") String reportType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam("groupType") String groupType,
            HttpServletResponse response) {

        try {
            LocalDateTime start;
            LocalDateTime end = LocalDateTime.now(); // 默认结束时间为当前时间

            // 如果提供了日期参数，尝试解析；否则按 groupType 推算
            if (startDate != null && !startDate.isEmpty()) {
                if (startDate.contains("T")) {
                    // 如果日期包含时间部分，直接解析为 LocalDateTime
                    start = LocalDateTime.parse(startDate);
                } else {
                    // 如果只有日期部分，补充时间为 00:00:00
                    start = LocalDate.parse(startDate).atStartOfDay();
                }
            } else {
                // 如果前端未传，根据 groupType 推算
                switch (groupType.toLowerCase()) {
                    case "day":
                        start = end.toLocalDate().atStartOfDay(); // 设置为今天零点
                        break;
                    case "week":
                        start = end.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(); // 当周的周一零点
                        break;
                    case "month":
                        start = end.withDayOfMonth(1).toLocalDate().atStartOfDay(); // 当月的第一天零点
                        break;
                    case "year":
                        start = end.withDayOfYear(1).toLocalDate().atStartOfDay(); // 当年的第一天零点
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid groupType: " + groupType);
                }
            }

            if (endDate != null && !endDate.isEmpty()) {
                if (endDate.contains("T")) {
                    end = LocalDateTime.parse(endDate);
                } else {
                    end = LocalDate.parse(endDate).atStartOfDay();
                }
            } else {
                end = LocalDateTime.now();
            }

            // 获取报表数据
            List<Map<String, Object>> reportData = reportService.getReportData(reportType, start, end, groupType);

            // 确定字段顺序（根据 reportType 动态定义字段）
            List<String> fieldOrder = determineFieldOrder(reportType);

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + reportType + "_report.xlsx");

            // 使用 Apache POI 生成 Excel 文件
            try (Workbook workbook = new XSSFWorkbook(); OutputStream out = response.getOutputStream()) {
                Sheet sheet = workbook.createSheet("Report");

                // 填充标题行
                Row header = sheet.createRow(0);
                for (int i = 0; i < fieldOrder.size(); i++) {
                    String fieldName = fieldOrder.get(i);
                    String translatedField = FieldTranslator.translate(fieldName); // 转换字段名称
                    header.createCell(i).setCellValue(translatedField);
                    System.out.println("Header field: " + translatedField);  // 调试打印
                }

// 填充数据行
                int rowIndex = 1;
                for (Map<String, Object> row : reportData) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    for (int i = 0; i < fieldOrder.size(); i++) {
                        String fieldName = fieldOrder.get(i);
                        Object value = row.getOrDefault(fieldName, "");
                        dataRow.createCell(i).setCellValue(value.toString());
                        System.out.println("Data for field " + fieldName + ": " + value);  // 调试打印
                    }
                }

                // 将文件写入响应输出流
                workbook.write(out);
            }
        } catch (Exception e) {
            // 处理异常
            System.err.println("Error occurred while exporting report: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private List<String> determineFieldOrder(String reportType) {
        switch (reportType.toUpperCase()) {
            case "DRAW_AMOUNT":
                return Arrays.asList("time_group", "gold_amount", "silver_amount", "bonus_amount", "other_amount");
            case "TOTAL_CONSUMPTION":
                return Arrays.asList("time_group", "total_amount");
            case "TOTAL_DEPOSIT":
                return Arrays.asList("time_group", "total_amount");
            case "USER_UPDATE_LOG":
                return Arrays.asList("time_group", "total_sliver_coin", "total_bonus");
            case "DAILY_SIGN_IN":
                return Arrays.asList("time_group", "total_sliver_coin");
            case "SLIVER_COIN_RECYCLE":
                return Arrays.asList("time_group", "total_sliver_coin");
            case "PRIZE_RECYCLE_REPORT":
                return Arrays.asList("time_group", "p_product_name",  "product_detail_name" , "grade", "total_sliver_coin");
            case "DRAW_RESULT_SUMMARY":
                return Arrays.asList("time_group", "product_name", "product_detail_name", "image_urls", "nickname");
            default:
                // 如果沒有定義的字段順序，返回一個默認值
                return Arrays.asList("time_group", "total_amount", "product_name", "nickname");
        }
    }



}

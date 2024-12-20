package com.one.onekuji.Report;

import com.one.onekuji.response.UserRes;
import com.one.onekuji.service.UserService;
import com.one.onekuji.util.SecurityUtils;
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
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    // 通用的報表查詢接口
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getReport(
            @RequestParam("reportType") String reportType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam("groupType") String groupType) { // 默认分页参数

        try {
            // 獲取當前用戶信息
            var userDetails = SecurityUtils.getCurrentUserPrinciple();
            assert userDetails != null;
            Long id = userDetails.getId();
            UserRes userById = userService.getUserById(id);

            // 獲取用戶角色 ID
            Long userRoleId = userById.getRoleId(); // 假設每個用戶只有一個角色

            // 根據 reportType 和角色進行權限校驗
            if ("TOTAL_DEPOSIT".equalsIgnoreCase(reportType)) {
                // TOTAL_DEPOSIT: 角色 1 和 2 都可查看
                if (!userRoleId.equals(1L) && !userRoleId.equals(2L)) {
                    throw new Exception("您無權查看此報表");
                }
            } else {
                // 其他類型: 只有角色 1 可查看
                if (!userRoleId.equals(1L)) {
                    throw new Exception("您無權查看此報表");
                }
            }

            LocalDateTime start;
            LocalDateTime end;

            // 如果提供了日期参数，尝试解析；否则按 groupType 推算
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay(); // 当天 00:00:00
            } else {
                // 根据 groupType 推算
                end = LocalDateTime.now();
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
                end = LocalDate.parse(endDate).atTime(LocalTime.MAX); // 当天 23:59:59
            } else {
                end = LocalDateTime.now();
            }

            // 调用服务层，处理时间范围，传递分页参数
            List<Map<String, Object>> reportData = reportService.getReportData(reportType, start, end, groupType);

            // 使用 FieldTranslator 将字段名转换为繁体中文
            List<Map<String, Object>> translatedData = new ArrayList<>();
            for (Map<String, Object> dataRow : reportData) {
                Map<String, Object> translatedRow = new HashMap<>();
                for (Map.Entry<String, Object> entry : dataRow.entrySet()) {
                    String translatedField = FieldTranslator.translate(entry.getKey()); // 转换字段名称
                    translatedRow.put(translatedField, entry.getValue()); // 将转换后的字段名称与对应的值放入新结果
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


            var userDetails = SecurityUtils.getCurrentUserPrinciple();
            assert userDetails != null;
            Long id = userDetails.getId();
            UserRes userById = userService.getUserById(id);

            // 獲取用戶角色 ID
            Long userRoleId = userById.getRoleId(); // 假設每個用戶只有一個角色

            // 根據 reportType 和角色進行權限校驗
            if ("TOTAL_DEPOSIT".equalsIgnoreCase(reportType)) {
                // TOTAL_DEPOSIT: 角色 1 和 2 都可查看
                if (!userRoleId.equals(1L) && !userRoleId.equals(2L)) {
                    throw new Exception("您無權查看此報表");
                }
            } else {
                // 其他類型: 只有角色 1 可查看
                if (!userRoleId.equals(1L)) {
                    throw new Exception("您無權查看此報表");
                }
            }


            LocalDateTime start;
            LocalDateTime end;

            // 设置开始时间
            if (startDate != null && !startDate.isEmpty()) {
                if (startDate.contains("T")) {
                    start = LocalDateTime.parse(startDate).with(LocalTime.MIN); // 设置为当天00:00:00
                } else {
                    start = LocalDate.parse(startDate).atStartOfDay(); // 设置为当天00:00:00
                }
            } else {
                start = determineStartDateByGroupType(groupType); // 根据 groupType 推算
            }

            // 设置结束时间
            if (endDate != null && !endDate.isEmpty()) {
                if (endDate.contains("T")) {
                    end = LocalDateTime.parse(endDate).with(LocalTime.MAX); // 设置为当天23:59:59
                } else {
                    end = LocalDate.parse(endDate).atTime(LocalTime.MAX); // 设置为当天23:59:59
                }
            } else {
                end = LocalDateTime.now().with(LocalTime.MAX); // 默认结束时间为当前时间的23:59:59
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
                }

                // 填充数据行
                int rowIndex = 1;
                for (Map<String, Object> row : reportData) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    for (int i = 0; i < fieldOrder.size(); i++) {
                        String fieldName = fieldOrder.get(i);
                        Object value = row.getOrDefault(fieldName, "");
                        dataRow.createCell(i).setCellValue(value.toString());
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

    // 根据 groupType 推算起始时间
    private LocalDateTime determineStartDateByGroupType(String groupType) {
        LocalDateTime now = LocalDateTime.now();
        switch (groupType.toLowerCase()) {
            case "day":
                return now.toLocalDate().atStartOfDay();
            case "week":
                return now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            case "month":
                return now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "year":
                return now.withDayOfYear(1).toLocalDate().atStartOfDay();
            default:
                throw new IllegalArgumentException("Invalid groupType: " + groupType);
        }
    }


    private List<String> determineFieldOrder(String reportType) {
        switch (reportType.toUpperCase()) {
            case "DRAW_AMOUNT":
                return Arrays.asList("time_group", "gold_amount", "silver_amount", "bonus_amount", "other_amount");
            case "TOTAL_CONSUMPTION":
                return Arrays.asList("time_group", "total_amount");
            case "TOTAL_DEPOSIT":
                return Arrays.asList("time_group", "total_amount", "nickname", "phone_number");
            case "USER_UPDATE_LOG":
                return Arrays.asList("time_group", "total_sliver_coin", "total_bonus");
            case "DAILY_SIGN_IN":
                return Arrays.asList("time_group", "total_sliver_coin");
            case "SLIVER_COIN_RECYCLE":
                return Arrays.asList("time_group", "total_sliver_coin");
            case "PRIZE_RECYCLE_REPORT":
                return Arrays.asList("time_group", "p_product_name", "product_detail_name", "grade", "total_sliver_coin");
            case "DRAW_RESULT_SUMMARY":
                return Arrays.asList("time_group", "product_name", "product_detail_name", "nickname", "amount_with_type");
            default:
                // 如果沒有定義的字段順序，返回一個默認值
                return Arrays.asList("time_group", "total_amount", "product_name", "nickname");
        }
    }




}

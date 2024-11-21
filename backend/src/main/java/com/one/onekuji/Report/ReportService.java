package com.one.onekuji.Report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private StoreConsumptionRepository storeConsumptionRepository;

    /**
     * 获取报表数据
     *
     * @param reportType 报表类型
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param groupType  分组类型
     * @return 查询的报表数据
     */
    public List<Map<String, Object>> getReportData(String reportType, LocalDateTime startDate, LocalDateTime endDate, String groupType) {
        switch (reportType) {
            case "DRAW_AMOUNT":
                return storeConsumptionRepository.getDrawAmountsByTimeRange(startDate, endDate);
            case "TOTAL_CONSUMPTION":
                return storeConsumptionRepository.getTotalConsumptionByTimeGroup(startDate, endDate, groupType);
            case "TOTAL_DEPOSIT":
                return storeConsumptionRepository.getTotalDepositByTimeGroup(groupType);
            case "USER_UPDATE_LOG":
                return storeConsumptionRepository.getUserUpdateLogSummary(groupType);
            case "DAILY_SIGN_IN":
                return storeConsumptionRepository.getDailySignInSummary(groupType);
            case "SLIVER_COIN_RECYCLE":
                return storeConsumptionRepository.getTotalSliverCoinByRecycleTime(groupType);
            case "PRIZE_RECYCLE_REPORT":
                return storeConsumptionRepository.getPrizeRecycleReport(groupType);
            case "DRAW_RESULT_SUMMARY":
                return storeConsumptionRepository.getDrawResultSummary(groupType);
            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }
    }
}

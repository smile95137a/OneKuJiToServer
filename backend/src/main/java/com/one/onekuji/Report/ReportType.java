package com.one.onekuji.Report;

public enum ReportType {
    PRIZE_CONSUMPTION,
    STORE_CONSUMPTION,       // 商城消费总额
    GACHA_CONSUMPTION,       // 扭蛋金币消费总额
    DEPOSIT_TOTAL,           // 储值金额总额
    AUTOMATIC_BONUS,         // 自动发放红利总额
    AUTOMATIC_SILVER,        // 自动发放银币总额（每日签到）
    RECYCLED_SILVER,         // 回收退回银币总额
    PRIZE_RECOVERY,          // 赏品回收报表
    CUSTOMER_RECORD          // 客人抽到的商品记录
}

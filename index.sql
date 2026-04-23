-- ============================================================
-- OneKuJi 效能優化 Index SQL
-- 請在 MySQL 手動執行此檔案
-- 執行前建議先用 EXPLAIN 確認查詢計畫
-- ============================================================

-- 商品表
CREATE INDEX idx_product_status           ON product(status);
CREATE INDEX idx_product_type             ON product(product_type);
CREATE INDEX idx_product_prize_category   ON product(prize_category);
CREATE INDEX idx_product_status_type      ON product(status, product_type);
CREATE INDEX idx_product_name             ON product(product_name);

-- 訂單表
CREATE INDEX idx_order_created_at         ON `order`(created_at);
CREATE INDEX idx_order_result_status      ON `order`(result_status);
CREATE INDEX idx_order_user_id            ON `order`(user_id);
CREATE INDEX idx_order_number             ON `order`(order_number);

-- 訂單明細表（修 N+1 必備）
CREATE INDEX idx_order_detail_order_id    ON order_detail(order_id);

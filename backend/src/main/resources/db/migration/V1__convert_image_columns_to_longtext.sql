-- Migration to support storing base64 data URLs in image-related columns.
-- Run these statements in your MySQL or MariaDB server. Adjust syntax if needed for other DBs.

-- store_product
ALTER TABLE store_product MODIFY COLUMN image_urls LONGTEXT;

-- product
ALTER TABLE product MODIFY COLUMN image_urls LONGTEXT;
ALTER TABLE product MODIFY COLUMN banner_image_url LONGTEXT;

-- product_detail
ALTER TABLE product_detail MODIFY COLUMN image_urls LONGTEXT;

-- news
ALTER TABLE news MODIFY COLUMN image_urls LONGTEXT;

-- draw_result
ALTER TABLE draw_result MODIFY COLUMN image_urls LONGTEXT;

-- banner
ALTER TABLE banner MODIFY COLUMN banner_image_urls LONGTEXT;

-- (Optional) For other tables with image column(s), make similar changes.
-- Example: ALTER TABLE some_table MODIFY COLUMN image_urls LONGTEXT;

-- Note: LONGTEXT should be sufficient for most use-cases, but consider storing images externally
-- (e.g., S3/CDN) for large and many images to avoid database bloat and performance degradation.

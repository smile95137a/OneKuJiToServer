# AWS S3 整合指南

本指南說明如何設定應用程式，使其將圖片上傳到 AWS S3 並由 S3 提供圖片連結，以及如何把資料庫中既有的圖片 URL 遷移到 S3。

## 設定屬性（後端 `application.properties`）

- `pictureFile.storage-type`：`local`（預設）或 `s3`。設定為 `s3` 時後端會將圖片上傳到 S3 並回傳 S3 URL。
- `pictureFile.path`：當使用 `local` 存放時的本地路徑，預設為 `uploads/`。
- `pictureFile.path-mapping`：對應於本地靜態資源的 URL 前綴，預設為 `/uploads/`。

S3 專屬屬性（當使用 S3 時填寫）：
- `s3.bucket-name`：S3 Bucket 名稱。
- `s3.region`：Bucket 所在區域（例如：`ap-northeast-1`）。
- `s3.access-key-id`：AWS Access Key ID。
- `s3.secret-access-key`：AWS Secret Access Key。
- `s3.endpoint-url`：可選的自定義 Endpoint（用於 MinIO、localstack 等 S3 相容服務）。
- `s3.use-path-style`：是否使用 Path-style 的位址（非 AWS 原生 S3 相容服務時常需設定）。

建議在生產環境透過環境變數設定憑證，開發/測試環境可放在 properties。

## 遷移工具（將現有 DB 中的 URL 轉換為 S3）

後端新增了一支遷移工具，功能如下：
- 掃描資料表：`store_product`、`product`、`product_detail`、`news`、`draw_result`、`banner` 等，檢查圖片 URL 欄位。
- 下載圖片（若是本地檔案，從 `pictureFile.path` 讀取；若是 public URL，則發出 HTTP 下載）。
- 將圖片上傳到 S3 並取得 S3 URL，然後更新資料庫中的圖片欄位為 S3 URL。

如何執行（範例）：
1. 編譯後端：`mvn -pl backend -am clean package`（在專案根目錄）。
2. 以 `s3` 儲存類型並提供 S3 憑證執行遷移工具（預設為 dry-run，只列出會做的更新）：

   ```bash
   java -jar backend/target/backend-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=dev \
     --pictureFile.storage-type=s3 \
     --s3.bucket-name=YOUR_BUCKET \
     --s3.region=ap-northeast-1 \
     --s3.access-key-id=YOUR_KEY \
     --s3.secret-access-key=YOUR_SECRET
   ```

   或使用 CLI 類別：

   ```bash
   java -cp backend/target/backend-0.0.1-SNAPSHOT.jar com.one.onekuji.s3.migration.S3DbMigrationTool \
     --pictureFile.storage-type=s3 --s3.bucket-name=YOUR_BUCKET ...
   ```

注意事項：
- 遷移工具假設圖片欄位的格式為 JSON 陣列（例如：`product.image_urls`）或在 `draw_result` 為逗號分隔的文字（CSV）。
- 預設遷移工具以 dry-run 模式執行（不會更新 DB），此行為由 `s3.migration.dry-run` 屬性控制（預設為 `true`）。若要允許資料庫寫入，請在命令行或 properties 設定 `--s3.migration.dry-run=false`。
- 預設會嘗試遷移遠端 HTTP(S) 圖片；若只要遷移本地檔案，請設定 `s3.migration.migrate-remote-urls=false`。
- 在執行遷移前請先於 staging 測試並備份 DB，避免不可逆的資料異動。
- 確認提供的 S3 憑證具備上傳（PutObject）權限，且 Bucket 可由後端寫入及讀取。

## 向 AWS 管理員需取得的資訊

請向擁有 AWS 存取權限的人索取以下資訊：
- S3 Bucket 名稱
- AWS Region
- AWS Access Key ID（具 PUT 權限）
- AWS Secret Access Key
- （選用）若使用 path-style 或 S3 相容服務，請同時提供 endpoint URL 與相關資訊

## 驗證

- 遷移完成後，檢查 DB 中是否已更新為 S3 URL（查幾筆資料確認）。
- 確認前端可以直接存取 S3 URL（若 Bucket 為私密，則需額外處理預簽名 URL 或 Proxy）。
# AWS S3 Integration Guide

This guide explains how to configure the application to upload and serve images from AWS S3 and how to migrate existing image URLs stored in the database to S3-hosted URLs.

## Properties (backend `application.properties`)

- `pictureFile.storage-type` : `local` (default) or `s3`. When set to `s3`, backend will upload images to S3 and return S3 URLs.
- `pictureFile.path` : local storage path used when `local` storage type is set. Default: `uploads/`.
- `pictureFile.path-mapping` : URL mapping prefix for local hosted resources. Default: `/uploads/`.

S3 specific properties (set these when using S3):
- `s3.bucket-name` : Your S3 bucket name.
- `s3.region` : AWS region for your bucket (e.g., `ap-northeast-1`).
- `s3.access-key-id` : AWS access key id.
- `s3.secret-access-key` : AWS secret access key.
- `s3.endpoint-url` : Optional custom endpoint (for S3 compatible services / MinIO / localstack).
- `s3.use-path-style` : If using path style addressing (for non-AWS S3 compatible services).

Set the credentials securely through environment variables in production, or via the properties file in dev/test.

## Migration Utility (Migrate existing DB URLs to S3)

We added a migration utility implemented in the backend module. It will:
- Scan tables: `store_product`, `product`, `product_detail`, `news`, `draw_result`, and `banner` for image URL columns
- Download the images (from local file system or any public URL) and upload them to S3
- Update the DB records to point to S3 URLs

How to run (example):
1. Build the backend: `mvn -pl backend -am clean package`  (from project root)
2. Run the migration tool with `s3` storage type and proper S3 credentials:

   java -jar backend/target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --pictureFile.storage-type=s3 --s3.bucket-name=YOUR_BUCKET --s3.region=ap-northeast-1 --s3.access-key-id=YOUR_KEY --s3.secret-access-key=YOUR_SECRET

Alternatively use the dedicated CLI class `S3DbMigrationTool`:

   java -cp backend/target/backend-0.0.1-SNAPSHOT.jar com.one.onekuji.s3.migration.S3DbMigrationTool --pictureFile.storage-type=s3 --s3.bucket-name=YOUR_BUCKET ...

Notes:
- The migration tool assumes image URL fields are either JSON arrays (typical for product/image lists) or comma-separated strings for the `draw_result` table.
- The migration tool will run in dry-run mode by default to avoid modifying your DB accidentally. This is controlled by the `s3.migration.dry-run` property (defaults to `true`). To actually perform DB updates, set `--s3.migration.dry-run=false` in the command line or in `application.properties`.
 - The migration tool will run in dry-run mode by default to avoid modifying your DB accidentally. This is controlled by the `s3.migration.dry-run` property (defaults to `true`). To actually perform DB updates, set `--s3.migration.dry-run=false` in the command line or in `application.properties`.
 - The migration tool will attempt to migrate remote HTTP(S) URLs by default; if you only want to migrate local files from your uploads folder, set `s3.migration.migrate-remote-urls=false`.
- Test the migration in a staging environment before running on production.
- Make sure S3 credentials are properly provided and the bucket is accessible.

## Required information to ask the AWS administrator

Please request the following from the person with access to the AWS account:
- S3 Bucket name
- AWS Region
- AWS Access Key ID (with sufficient permissions to put objects)
- AWS Secret Access Key
- (Optional) Endpoint URL and info if using path-style addressing or a S3 compatible service

## Verify

- After migration, check a few records in DB to confirm the image URLs are updated and accessible.
- Ensure the frontend serves image URLs directly from S3.

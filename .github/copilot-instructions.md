```instructions
# OneKuJi 一番賞抽獎系統 — AI 開發指引

## 專案架構總覽
多模組 Spring Boot 3.3.1 (Java 17) Maven 專案 — **關鍵職責分離**：
- `backend`: 管理後台 API + MyBatis 資料層 (`@MapperScan("com.one.onekuji.repository")`)
- `frontend`: 使用者商店 API + JPA repositories + ECPay 金流整合
- `common`: 跨模組共用的 DTO、Controller、Service（可被 backend/frontend 依賴）

**核心業務邏輯**：一番賞（Ichiban Kuji）盲盒抽獎系統，每個產品有多個獎品等級（A/B/C/LAST 等），使用者購買抽獎券抽取獎品。

## 快速啟動（開發環境）
```cmd
# 建置所有模組
mvn clean package -DskipTests

# 啟動後端（管理後台，port 8080）
mvn -pl backend -am spring-boot:run

# 啟動前端（使用者商店，port 8081）
mvn -pl frontend -am spring-boot:run

# 設定 DB（Windows）
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/onekuji?serverTimezone=UTC^&useUnicode=true^&characterEncoding=utf-8^&useSSL=true
```

**測試流程**：Swagger UI at `http://localhost:8080/swagger-ui/index.html#/` → POST `/api/auth/login` 取得 JWT token → 在 Authorize 設定 Bearer token。

## 核心業務邏輯：獎品編號系統（Prize Number System）

**理解這個系統是修改產品/抽獎邏輯的關鍵**：

### 資料結構
- `product`: 產品主檔（一番賞商品，例如「海賊王一番賞」）
- `product_detail`: 獎品項目（等級、數量、圖片）— 一個產品有多個等級的獎品
- `prize_number`: 獎品編號池（每個實體獎品對應一個編號，用於抽獎）
- `draw_result`: 抽獎結果記錄

### 獎品編號生成邏輯（`ProductDetailService.regeneratePrizeNumbers()`）
1. **何時觸發**：新增/更新 `product_detail` 且產品未抽獎/未上架時
2. **運作方式**：
   - 刪除該產品所有舊的 `prize_number` 記錄
   - 根據每個 `product_detail.quantity` 生成對應數量的編號（例如 A 賞 5 個 → 生成 5 個編號）
   - **LAST 獎不參與編號池**（`grade = 'LAST'` 會被排除）
   - 編號隨機打散（`Collections.shuffle`）後批次寫入 DB
3. **限制**：產品已上架（`AVAILABLE`）或有人已抽獎（`prize_number.is_drawn = true`）時，**不可重新生成編號**，只能更新銀幣/尺寸/概率欄位

### 抽獎流程（`DrawResultService.handleDraw2()`）
1. 使用者選擇獎品編號（從前端傳入的 `prizeNumbers` 字串陣列）
2. 檢查編號是否已被抽走（`is_drawn` 欄位）
3. 扣款 → **按概率計算實際抽中的獎品**（`drawPrizeForNumber()`）
4. 更新 `prize_number.is_drawn = true` + `product_detail.quantity -= 1`
5. 寫入 `draw_result` 記錄 + 發送 WebSocket 訊息（`/topic/lottery`）
6. 特殊處理：所有獎品抽完後自動抽 LAST 賞（`handleSPPrize()`）

**關鍵查詢**：`PrizeNumberMapper.getPrizeNumbersByProductIdAndNumbers()` 批次取得使用者選的編號。

## 圖片儲存架構（Strategy Pattern 實作）

**重要**：專案使用 **可切換的儲存後端**，透過 `pictureFile.storage-type` 決定本地或 S3。

### 架構設計
```
ImageUtil (static facade)
    ↓ 委派給
ImageStorageService (interface)
    ↓ 實作
LocalImageStorageService (@ConditionalOnProperty "local")
S3ImageStorageService (@ConditionalOnProperty "s3")
```

### 實際使用（Controllers）
```java
// 所有 Controller 統一使用 ImageUtil 靜態方法
String url = ImageUtil.upload(multipartFile);              // 400x400 正方形
String editorUrl = ImageUtil.uploadForCKEditor(file);      // 原尺寸（富文本編輯器）
String bannerUrl = ImageUtil.uploadRectangle(file);        // 600x300 矩形
```

### 生產環境配置（S3 模式）
```properties
pictureFile.storage-type=s3
s3.bucket-name=onemorelottery-img
s3.region=us-east-1
s3.access-key-id=                    # 空白表示使用 EC2 IAM Role
s3.secret-access-key=                # 同上
```

**回傳格式**：S3 模式只回傳 **相對路徑** `images/<filename>`，前端自行組合 `https://onemorelottery.tw/images/<filename>`（隱藏 S3 bucket 資訊）。

### S3 遷移工具
```cmd
# 將既有 DB 圖片 URL 遷移至 S3（dry-run 模式）
java -cp backend/target/backend-0.0.1-SNAPSHOT.jar com.one.onekuji.s3.migration.S3DbMigrationTool

# 實際執行遷移
java -cp backend/target/backend-0.0.1-SNAPSHOT.jar com.one.onekuji.s3.migration.S3DbMigrationTool --s3.migration.dry-run=false
```

掃描資料表：`store_product`, `product`, `product_detail`, `news`, `draw_result`, `banner`。

## API 回應規範
**所有** Controller 必須使用統一回應格式：
```java
@PostMapping("/add")
public ResponseEntity<ApiResponse<ProductRes>> addProduct(@RequestBody ProductReq req) {
    ProductRes result = productService.add(req);
    return ResponseUtils.success(result);  // 或 ResponseUtils.failure("error message")
}
```

`ApiResponse<T>` 結構：`{ code: 200, message: "success", data: T }`

## 資料存取模式（混合使用）

### MyBatis（Backend 主要使用）
```java
@Mapper  // 必須加此註解
public interface ProductMapper {
    @Select("SELECT * FROM product WHERE product_id = #{id}")
    ProductRes getProductById(Long id);
    
    @Insert("INSERT INTO product (...) VALUES (...)")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    void insert(ProductReq req);
}
```
- `@MapperScan` 設定在 `BackendApplication` / `FrontEndApplication`
- MyBatisConfig 啟用 `mapUnderscoreToCamelCase = true`（DB snake_case ↔ Java camelCase）

### JPA（Frontend/Common 使用）
用於簡單 CRUD 或關聯查詢。

## 安全與認證
- JWT 驗證：`JwtAuthenticationFilter` 攔截請求，從 `Authorization: Bearer <token>` 驗證
- 產生 JWT secret：執行 `backend/src/main/java/PasswordEncoderDemo.java` 或 `GenerateJwtSecret.main()`
- **CORS 設定**：`SpringSecurityConfig` / `CorsConfig` 允許 `https://onemorelottery.tw` 與 `http://localhost:5173`
- **注意**：目前 `SpringSecurityConfig.securityFilterChain()` 設定 `.requestMatchers("/**").permitAll()`，**生產環境需限縮權限**

## 支付整合（ECPay）
- 位置：`frontend/src/main/java/com/one/frontend/ecpay`
- 設定檔：`frontend/src/main/resources/payment_conf.xml`（包含測試/正式 URL）
- 相關金鑰請透過環境變數或 application.properties 設定

## 資料庫管理
**無自動 Migration 工具**（無 Flyway/Liquibase）— **所有 schema 變更需手動執行 SQL**。

新增欄位步驟：
1. 手動 ALTER TABLE
2. 更新對應的 `@Mapper` SQL 語句
3. 更新 DTO class 新增欄位

## 開發工作流程

### 新增功能（典型流程）
1. **定義 DTO**：在 `model` / `request` / `response` 建立 Java class
2. **寫 Mapper**：`@Mapper` interface + SQL 註解或 XML
3. **寫 Service**：業務邏輯，呼叫 Mapper
4. **寫 Controller**：`@RestController` + `ResponseUtils.success/failure`
5. **測試**：透過 Swagger UI 測試

### 修改獎品系統
**警告**：若產品已上架或已有抽獎記錄，`ProductDetailService.regeneratePrizeNumbers()` 不會執行。

修改限制判斷邏輯：
```java
// ProductDetailService.updateProductDetail()
boolean isDrawnPrize = prizeNumberMapper.isTrue(productId).stream().anyMatch(PrizeNumber::getIsDrawn);
boolean isProductAvailable = product.getStatus().equals(ProductStatus.AVAILABLE);
```

### 除錯技巧
- **SQL 查詢紀錄**：在 `application.properties` 設定 `logging.level.com.one.onekuji.repository=DEBUG`
- **圖片上傳失敗**：檢查 `pictureFile.storage-type` 與對應的 path/bucket 設定
- **JWT 驗證失敗**：確認 `app.jwt-secret` 為 base64 編碼，且前端 header 正確設定 `Authorization: Bearer <token>`

## 關鍵檔案清單
```
pom.xml                                          # 多模組定義
backend/src/main/java/com/one/onekuji/
  ├── BackendApplication.java                    # 後端啟動類（@MapperScan）
  ├── config/
  │   ├── SpringSecurityConfig.java              # 安全配置（CORS + JWT filter）
  │   ├── S3Config.java                          # S3Client bean（支援 IAM Role）
  │   └── URLConfig.java                         # 本地檔案靜態資源映射（僅 local 模式）
  ├── util/
  │   ├── ImageUtil.java                         # 圖片上傳 facade（靜態方法委派）
  │   ├── ResponseUtils.java                     # API 回應工具
  │   └── storage/
  │       ├── ImageStorageService.java           # 儲存介面
  │       ├── LocalImageStorageService.java      # 本地檔案實作
  │       └── S3ImageStorageService.java         # S3 實作
  ├── service/ProductDetailService.java          # 獎品編號生成邏輯
  ├── repository/PrizeNumberMapper.java          # 獎品編號 Mapper
  └── s3/migration/S3MigrationService.java       # S3 遷移工具

frontend/src/main/java/com/one/frontend/
  ├── FrontEndApplication.java                   # 前端啟動類
  ├── ecpay/payment/integration/                 # ECPay 金流整合
  └── service/DrawResultService.java             # 抽獎核心邏輯

common/src/main/java/com/one/
  ├── controller/                                # 共用 Controller
  ├── model/                                     # 共用 DTO
  └── service/DrawResultService.java             # 抽獎服務（前後端共用）
```

## 非顯而易見的重要細節

### 1. 獎品編號不可逆
`regeneratePrizeNumbers()` 會刪除舊編號重新生成 — **已上架商品禁止執行**（會導致使用者已選的編號失效）。

### 2. LAST 獎特殊處理
- LAST 獎不進入編號池（`shouldIncludeInPrizeNumbers()` 判斷）
- 所有獎品抽完後觸發 `handleSPPrize()` 自動放入獎品盒

### 3. 圖片 URL 映射差異
- **Local 模式**：回傳 `/uploads/uuid_file.jpg`（由 `URLConfig` 映射到實體路徑）
- **S3 模式**：回傳 `images/uuid_file.jpg`（前端自行加上 domain）

### 4. MyBatis TypeHandler
`ListTypeHandler` / `StringListConverter` 處理 JSON 陣列與資料庫 TEXT 欄位的轉換（例如 `imageUrls`）。

### 5. WebSocket 抽獎通知
抽獎成功後發送訊息到 `/topic/lottery`，前端即時顯示其他使用者抽獎結果（跑馬燈效果）。

## 常見錯誤與解法

| 錯誤訊息 | 原因 | 解法 |
|---------|------|------|
| `ImageStorageService delegate is not initialized` | Spring context 未啟動或 storage-type 未設定 | 確認在 Spring 環境執行 + 設定 `pictureFile.storage-type` |
| `部分指定的獎品編號不存在或重複` | 前端傳入的編號不在 DB 或已被抽走 | 檢查 `prize_number` 表 + `is_drawn` 狀態 |
| `S3 bucket name is not configured` | S3 模式但未設定 bucket | 設定 `s3.bucket-name` |
| `所有獎品都已抽完` | 該產品所有 `product_detail.quantity = 0` | 正常邏輯，應觸發 LAST 獎 |

## 生產部署檢查清單
- [ ] 設定 `app.jwt-secret`（base64 編碼）
- [ ] 修改 `SpringSecurityConfig` 限縮 `permitAll()` 範圍
- [ ] 確認 `s3.bucket-name` 與 `s3.region` 正確
- [ ] 檢查 CORS `allowedOrigins` 僅包含生產 domain
- [ ] 執行 S3 遷移工具（先 dry-run 測試）
- [ ] 手動執行 DB schema 變更

```

Repo snapshot: multi-module Java (Maven) project — modules: `common`, `backend`, `frontend`.

Quick start
- Build: mvn -DskipTests package
- Run backend: mvn -pl backend -am spring-boot:run (or run `BackendApplication`); run frontend: mvn -pl frontend -am spring-boot:run
- Windows env example: set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/onekuji?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true

Where responsibilities live
- `backend`: admin APIs, MyBatis mappers (`com.one.onekuji.repository`) and services
- `frontend`: storefront, ECPay payment integration (`com.one.frontend.ecpay`) and JPA repos
- `common`: shared DTOs/controllers used across services

Key quick references
- Use `ResponseUtils.success/failure` (ApiResponse) for controller results: see `backend/src/main/java/com/one/onekuji/util/ResponseUtils.java`.
- Add MyBatis mapper: `@Mapper` interface in `backend/.../repository` using `@Select/@Insert/@Update`.
- Uploads: `ImageUtil` expects `pictureFile.path` + `pictureFile.path-mapping` (or fallback `file.upload-dir`).
- JWT: `JwtTokenProvider`, `JwtAuthenticationFilter`, `GenerateJwtSecret.java` to generate a base64 secret.
- ECPay: `frontend/src/main/java/com/one/front.../ecpay/...` and `EcpayPayment.xml` for env-specific endpoints.

Gotchas
- No DB migration tool (manual DB/schema changes required).
- Security config currently permits everything; verify `SpringSecurityConfig` for production readiness.

If you want a short starter task (backend end-to-end):
1) Set DB env vars, 2) set `pictureFile.path=/absolute/path/uploads` + `pictureFile.path-mapping=/uploads/`, 3) run backend, 4) login at POST /api/auth/login and test APIs via Swagger.

Important config (common locations)
- Backend props: `backend/src/main/resources/application.properties` (DB, jwt, logging, file mappings)
- Frontend props: `frontend/src/main/resources/application.properties` (same shape, env placeholders)
- Picture/static mapping: `pictureFile.path` (filesystem), `pictureFile.path-mapping` (URL prefix). Controllers and `ImageUtil` expect these.
- DB: MySQL recommended (see properties). No migration tool: update schema manually.

Key patterns and conventions
- REST controllers live in `.../controller` and use `@RequestMapping("/api/...")`.
- Services live in `.../service` and are wired with Spring DI. Use `@AllArgsConstructor` or constructor injection.
- Data access: two patterns are used:
  - MyBatis mappers under `repository` with `@Mapper` and SQL (annotation-style or scripts) – MapperScan configured in the app classes.
  - JPA repositories (frontend/common) for dto packages.
- DTO naming: `*Req` = request objects, `*Res` = response objects, `DTO` used for JPA entities/transfer.
- Responses: controllers return `ApiResponse<T>` built using `ResponseUtils.success/failure` (see `backend/src/main/java/com/one/onekuji/util/ResponseUtils.java`).

Security / Auth
- JWT handled by `JwtTokenProvider`, `JwtAuthenticationFilter`; secret config `app.jwt-secret` must be base64 (see `GenerateJwtSecret.java` to create one).
- Default login endpoint for admin: POST `/api/auth/login` returning `ApiResponse<JWTAuthResponse>`.
- Note: in `SpringSecurityConfig` many endpoints are currently set to permitAll — review for production.

File uploads & HTML
- HTML editor content is sanitized/processed by `HtmlProcessor` and images are saved via `ImageUtil`. `ImageUtil` depends on `pictureFile.path`/`pictureFile.path-mapping`.
- Many controllers accept MultipartFile lists (`@RequestPart(value = "images")`) and call `ImageUtil.upload()`.

Payment integration
- `frontend` contains ECPay payment integration under `com.one.frontend.ecpay.payment.integration`. `EcpayPayment.xml` contains Test/Production URLs; set your keys via config.

Typical changes and examples
- New controller skeleton:
  - File: `backend/src/main/java/com/one/onekuji/controller/FooController.java`
  - Pattern: `@RestController`, `@RequestMapping("/api/foo")`, return `ResponseEntity<ApiResponse<...>>` and use `ResponseUtils.success(...)`.
- New MyBatis mapper:
  - File: `backend/src/main/java/com/one/onekuji/repository/BarMapper.java`
  - Annotate `@Mapper`, add `@Select`/`@Insert`/`@Update` statements and return DTOs in `model`, `response` or `request` packages.

Debugging + workflows
- Use Swagger UI for API exploration: `http://localhost:8080/swagger-ui/index.html#/` (backend). Use logging-level settings in application.properties for query debugging (MyBatis/Hibernate).
- Generate JWT secret locally: run `GenerateJwtSecret.main()` and copy base64 into `app.jwt-secret` or env var.
- If uploads or image mapping fails, ensure `pictureFile.path` and `pictureFile.path-mapping` are set. For dev set `pictureFile.path=uploads/` and `pictureFile.path-mapping=/uploads/`.

Where to look first (core files)
- Root: `pom.xml` — multi-module setup
- Backend: `backend/src/main/java/com/one/onekuji/BackendApplication.java` (MapperScan), `application.properties`, `SpringSecurityConfig`, `JwtTokenProvider`, `ImageUtil`, `ResponseUtils`, `ProductDetailRepository`.
- Frontend: `frontend/src/main/java/com/one/frontend/FrontEndApplication.java`, `OrderService` and `ecpay` package, `application.properties`.
- Common: `common/src/main/java/com/one/CommonEndApplication.java` – shared controllers/DTOs.

Non-obvious quirks / gotchas
- The codebase mixes MyBatis and JPA in different modules – pick the correct pattern per module (MyBatis for backend repository folder, JPA for frontend/common dto repo).
- `ImageUtil` reads `pictureFile.path` & `pictureFile.path-mapping` instead of `file.upload-dir` – set both when debugging file uploads.
- `GlobalExceptionHandler` returns ApiResponse objects directly (code/message), while controllers return ResponseEntity<ApiResponse<T>> — prefer the controller pattern for new endpoints.

If anything is unclear, ask for a specific task or file to update and I’ll implement or improve the local dev & run scripts (win/cmd examples included).

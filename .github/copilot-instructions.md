```instructions

倉庫快照：多模組 Java (Maven) 專案 — 模組：`common`、`backend`、`frontend`。

快速開發 (Quick start)
- 建置：mvn -DskipTests package
- 啟動後端：mvn -pl backend -am spring-boot:run（或直接執行 `BackendApplication`）；啟動前端：mvn -pl frontend -am spring-boot:run
- Windows 範例環境變數：
  set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/onekuji?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true

各模組責任範圍
- `backend`：管理後台 API、MyBatis Mapper（`com.one.onekuji.repository`）與 Service 層
- `frontend`：前端商店頁面、ECPay 金流整合（`com.one.frontend.ecpay`）與 JPA repository
- `common`：共用 DTO、Controller

快速參考
- Controller 統一使用 `ResponseUtils.success/failure` （ApiResponse）：參考 `backend/src/main/java/com/one/onekuji/util/ResponseUtils.java`。
- 新增 MyBatis Mapper：在 `backend/.../repository` 新增 `@Mapper` 介面，使用 `@Select/@Insert/@Update`。
- 檔案上傳：`ImageUtil` 預期會使用 `pictureFile.path` 與 `pictureFile.path-mapping`，若未設定則會 fallback 至 `file.upload-dir`。
- JWT：`JwtTokenProvider`、`JwtAuthenticationFilter`、`GenerateJwtSecret.java` 可用於建立 base64 的 JWT secret。
- ECPay：`frontend` 中 `com.one.frontend.ecpay` 與 `EcpayPayment.xml`（包含 Test/Prod 端點），相關金流金鑰請透過組態設定。

注意事項
- 本專案沒有自動 DB migration 工具（需人工更新 schema）；請注意資料庫結構變更。
- `SpringSecurityConfig` 預設許多端點為 permitAll，部署前請依實際情況檢視權限設計與安全設定。

簡單後端測試流程（從 0 到 1）
1) 設定 DB 環境變數
2) 設定 `pictureFile.path=/absolute/path/uploads` 與 `pictureFile.path-mapping=/uploads/`（開發環境）
3) 啟動後端
4) 使用 POST `/api/auth/login` 登入並透過 Swagger 測試 API

重要的設定檔位置
- 後端組態（Backend props）：`backend/src/main/resources/application.properties`（包含 DB、JWT、log、檔案 mappings）
- 前端組態（Frontend props）：`frontend/src/main/resources/application.properties`（結構相同）
- 圖片 / 靜態對應：`pictureFile.path`（實體檔案路徑）與 `pictureFile.path-mapping`（URL 前綴）
- DB：建議使用 MySQL，開發可使用本地資料庫；無自動 migration，請視需求改變 schema

主要模式與慣例
- REST controllers 位於 `.../controller`，使用 `@RequestMapping("/api/...")`。
- Service 層位於 `.../service`，使用 Spring DI，建議使用建構子注入或 `@AllArgsConstructor`。
- 資料存取：
  - MyBatis (後端）：`repository` 下使用 `@Mapper` 與 SQL 片段或注解。
  - JPA (frontend/common）：用於 DTO/Repository 操作。
- DTO 命名規則：`*Req` = request、`*Res` = response；`DTO`用於 JPA entity / 資料傳遞。
- API 回應：控制器回傳 `ApiResponse<T>`，可使用 `ResponseUtils.success/failure` 製造回應。

安全 / 認證
- 使用 JWT（`JwtTokenProvider`、`JwtAuthenticationFilter`）；JWT secret 請使用 base64 編碼的 `app.jwt-secret`（可使用 `GenerateJwtSecret` 產生）。
- 後台預設登入 API: POST `/api/auth/login`，回傳 `ApiResponse<JWTAuthResponse>`。
- 注意：在 `SpringSecurityConfig` 設為 permitAll 的端點需在正式環境重新檢查權限政策。

檔案上傳與 HTML 編輯器
- HTML 內容會經 `HtmlProcessor` 清洗/處理，圖片會由 `ImageUtil` 上傳並回傳圖片 URL。`ImageUtil` 依賴 `pictureFile.path` + `pictureFile.path-mapping`。
- 許多 Controller 接受 MultipartFile（例如 `@RequestPart("images")` ，會呼叫 `ImageUtil.upload()`）。

支付整合
- frontend 中 `com.one.frontend.ecpay.payment.integration` 為 ECPay 整合位置；相關的 `EcpayPayment.xml` 含測試/正式端點，請在組態中提供金流金鑰。

常見變更與範例
- 新增 Controller 範例：
  - 檔案：`backend/src/main/java/com/one/onekuji/controller/FooController.java`
  - 樣式：`@RestController`, `@RequestMapping("/api/foo")`, 回傳 `ResponseEntity<ApiResponse<...>>` 與使用 `ResponseUtils.success(...)`。
- MyBatis Mapper 範例：
  - 檔案：`backend/src/main/java/com/one/onekuji/repository/BarMapper.java`
  - 標註 `@Mapper`，使用 `@Select/@Insert/@Update` 並回傳 DTO。

除錯與開發工作流程
- 使用 Swagger UI 測試 API（後端）：`http://localhost:8080/swagger-ui/index.html#/`。
- 使用組態調整 LOG 等級觀察 MyBatis/Hibernate SQL；必要時將 LOG level 設高。
- 產生 JWT secret：執行 `GenerateJwtSecret.main()`，把輸出填回 `app.jwt-secret`。
- 若上傳失敗請檢查 `pictureFile.path` 與 `pictureFile.path-mapping` 是否正確設定；開發環境可把 `pictureFile.path=uploads/` 與 `pictureFile.path-mapping=/uploads/`。

優先查看（核心檔案）
- 專案根：`pom.xml`（多模組）
- 後端：`BackendApplication.java`（MapperScan）、`application.properties`、`SpringSecurityConfig`、`JwtTokenProvider`、`ImageUtil`、`ResponseUtils` 等。
- 前端：`FrontEndApplication.java`、 `OrderService` 以及 ECPay 相關程式碼與設定檔。
- 共用：`common/src/main/java/...` 包含共用 Controller / DTO。

不明顯但重要的細節
- 此專案後端同時使用 MyBatis 與 JPA（依模組不同），請根據模組類型選擇合適的資料存取策略。
- `ImageUtil` 會讀 `pictureFile.path` 與 `pictureFile.path-mapping`（而非 `file.upload-dir`），開發時需確認兩種配置是否一致。
- `GlobalExceptionHandler` 會回傳 `ApiResponse` 物件 (程式錯誤為「code + message」格式)；在新增 Controller 時，建議維持 Controller 回傳 `ResponseEntity<ApiResponse<T>>` 的慣例。

若有任何疑問，請指定要修改或檢視的檔案與需求，我會就該範圍進行改良或提供更完整的開發/執行指引。

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

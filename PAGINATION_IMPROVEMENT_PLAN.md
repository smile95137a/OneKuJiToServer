# 後端分頁改善計畫 — 分析與討論

> 分支：`feature/backend-pagination`  
> 建立日期：2026-04-23  
> 狀態：**進行中**

---

## 一、現況問題分析

### 1.1 商品頁面（Product）

| 端點 | 目前行為 | 問題 |
|------|----------|------|
| `GET /api/product/query` | 回傳**全部**商品 | 商品數量大時一次傳大量 JSON |
| `POST /api/product/type` | 依 type 回傳**全部** | 同上 |
| `POST /api/product/OneKuJi/type` | 依 prizeCategory 回傳**全部** | 同上 |

**程式碼問題**：`ProductRepository.selectAllProducts()` 直接 `SELECT * FROM product`，無任何分頁、無 LIMIT。

---

### 1.2 訂單頁面（Order）

| 端點 | 目前行為 | 問題 |
|------|----------|------|
| `GET /api/order/query` | 回傳**全部**訂單（只能過濾日期） | 訂單數量越大越慢 |

**程式碼問題（嚴重）— N+1 Query**：

```java
// OrderService.getAllOrders() — 目前寫法
list.stream().map(order -> {
    order.setOrderDetails(orderDetailMapper.findOrderDetailsByOrderId(order.getId())); // 查詢一次
    order.setOrderCount(orderDetailMapper.findOrderDetailsByOrderId(order.getId()).stream().count()); // 又查一次！
    ...
})
```

假設有 500 筆訂單，這段程式碼會產生 `1 + 500*2 = 1001` 次 DB 查詢。

---

### 1.3 目前無 DB Index

目前 SQL 中沒有針對以下常用查詢欄位加 Index：
- `product.status`（列表排序頻繁用到）
- `product.product_type`
- `product.prize_category`
- `order.created_at`（按日期過濾）
- `order.result_status`
- `order_detail.order_id`（N+1 問題根源）

---

## 二、我的建議方案

### 2.1 統一分頁 Request / Response 結構

**分頁請求（通用）**：
```java
public class PageReq {
    private int page = 1;    // 第幾頁（1-based）
    private int size = 10;   // 每頁幾筆
    private String sortBy;   // 排序欄位
    private String sortDir = "DESC";  // ASC / DESC
}
```

**分頁回應（通用 Wrapper）**：
```json
{
  "code": 200,
  "data": {
    "list": [...],
    "total": 385,
    "page": 1,
    "size": 10,
    "totalPages": 39
  }
}
```

---

### 2.2 商品查詢改善方案

新的端點設計（合併現有三個查詢端點為一個）：

```
GET /api/product/query?page=1&size=10&type=PRIZE&prizeCategory=A&status=AVAILABLE
```

**後端 MyBatis SQL（使用動態 SQL）**：
```sql
SELECT * FROM product
WHERE 1=1
  AND (#{type} IS NULL OR product_type = #{type})
  AND (#{prizeCategory} IS NULL OR prize_category = #{prizeCategory})
  AND (#{status} IS NULL OR status = #{status})
ORDER BY
  CASE WHEN status='AVAILABLE' THEN 1
       WHEN status='NOT_AVAILABLE_YET' THEN 2
       WHEN status='SOLD_OUT' THEN 3
       ELSE 4 END,
  product_id DESC
LIMIT #{offset}, #{size}
```

加上 COUNT 查詢取得總筆數：
```sql
SELECT COUNT(*) FROM product WHERE 1=1 AND ...（同上條件）
```

---

### 2.3 訂單查詢改善方案

新端點：
```
GET /api/order/query?page=1&size=20&startDate=2026-01-01&endDate=2026-04-23&resultStatus=SHIPPED
```

**N+1 修復方案：使用 JOIN 一次取完**：
```sql
SELECT o.*, 
       COUNT(od.id) AS order_count
FROM `order` o
LEFT JOIN order_detail od ON od.order_id = o.id
WHERE 1=1
  AND (#{startDate} IS NULL OR o.created_at >= #{startDate})
  AND (#{endDate} IS NULL OR o.created_at <= #{endDate})
  AND (#{resultStatus} IS NULL OR o.result_status = #{resultStatus})
GROUP BY o.id
ORDER BY o.created_at DESC
LIMIT #{offset}, #{size}
```

Order Details（子項目）改為前端點進去單筆訂單才查，不在列表頁一次撈。

---

### 2.4 建議 Index SQL

```sql
-- 商品表
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_type ON product(product_type);
CREATE INDEX idx_product_prize_category ON product(prize_category);
CREATE INDEX idx_product_status_type ON product(status, product_type);

-- 訂單表
CREATE INDEX idx_order_created_at ON `order`(created_at);
CREATE INDEX idx_order_result_status ON `order`(result_status);
CREATE INDEX idx_order_user_id ON `order`(user_id);

-- 訂單明細表
CREATE INDEX idx_order_detail_order_id ON order_detail(order_id);
```

---

### 2.5 其他額外建議

| 項目 | 說明 | 優先級 |
|------|------|--------|
| **快取（Cache）** | 商品列表可加 Spring Cache (`@Cacheable`) + Redis，TTL 5分鐘，修改時 evict | 中 |
| **訂單詳情延遲載入** | 訂單列表不帶明細，點開單筆訂單再打 `GET /api/order/{id}` | 高（配合分頁一起做）|
| **前端 keepAlive** | 避免重複打 API（前端配合，後端不需要動） | 前端處理 |
| **DB 連線池調整** | `HikariCP` 預設 10 條，訂單並發高時可調整 | 低 |

---

## 三、修改範圍（目前計畫）

### 商品頁面需改動的檔案：

```
backend/.../controller/ProductController.java     ← 修改端點參數
backend/.../service/ProductService.java           ← 加分頁邏輯
backend/.../repository/ProductRepository.java     ← 加分頁 SQL
backend/.../request/ProductQueryReq.java          ← 新增（含分頁+過濾參數）
backend/.../response/PageRes.java                 ← 新增（通用分頁 wrapper）
```

### 訂單頁面需改動的檔案：

```
backend/.../controller/OrderController.java       ← 修改端點參數
backend/.../service/OrderService.java             ← 修 N+1、加分頁
backend/.../repository/OrderRepository.java       ← 改 SQL
backend/.../request/OrderQueryReq.java            ← 加 page/size/resultStatus
```

### 新增 SQL（手動執行）：

```
index.sql   ← 所有 INDEX 語句（需手動在 DB 執行）
```

---

## 四、確認結果（已回答）

1. **端點**：保持現有三個端點不變，各自加分頁，最小改動原則。
2. **預設筆數**：預設 20 筆，前端可自由傳 `size`（上限 100）。
3. **訂單明細**：列表頁不帶明細，點進去才打 `GET /api/order/{id}` 取得。
4. **排序**：僅 `created_at DESC`，不需其他排序。
5. **前端框架**：前台 React、後台 Vue，分頁欄位以後端命名為準（`page` / `size` / `total` / `totalPages`）。
6. **搜尋**：訂單支援 `orderNumber` 模糊搜尋、商品支援 `productName` 模糊搜尋。
7. **查詢端點**：含分頁參數的查詢一律改為 `POST`。

---

## 五、執行順序（確認問題後）

```
Step 1: 建立通用 PageRes.java、更新 OrderQueryReq / 新增 ProductQueryReq
Step 2: 修改 ProductRepository SQL（動態分頁查詢）+ 對應 Service/Controller
Step 3: 修改 OrderRepository SQL（修 N+1 + 加分頁）+ 對應 Service/Controller
Step 4: 產出 index.sql（請手動在 DB 執行）
Step 5: 測試 Swagger UI
```


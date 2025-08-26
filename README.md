啟動時，swagger的網址:http://localhost:8080/swagger-ui/index.html#/


/**
 * 添加產品細節，並生成獎品編號。
 * @param detailReqs 包含產品詳細信息的請求列表。
 * @return 返回插入後的詳細響應列表。
 */
public List<DetailRes> addProductDetails(List<DetailReq> detailReqs) {
    List<DetailRes> detailResList = new ArrayList<>();
    Integer productId = Integer.valueOf(detailReqs.get(0).getProductId());
    
    try {
        // 1. 區分新增和更新的項目
        List<DetailReq> newItems = new ArrayList<>();
        List<DetailReq> updateItems = new ArrayList<>();
        
        for (DetailReq detailReq : detailReqs) {
            if (detailReq.getProductDetailId() != null) {
                updateItems.add(detailReq);
            } else {
                newItems.add(detailReq);
            }
        }
        
        // 2. 先處理新增和更新的 product_detail
        for (DetailReq detailReq : detailReqs) {
            // 轉義 HTML 字符
            detailReq.setDescription(escapeTextForHtml(detailReq.getDescription()));
            detailReq.setSpecification(escapeTextForHtml(detailReq.getSpecification()));
            detailReq.setStockQuantity(detailReq.getQuantity());
            detailReq.setSize(detailReq.getSize());
            
            if(detailReq.getIsPrize() == null){
                detailReq.setIsPrize(String.valueOf(false));
            }
            
            if (detailReq.getProductDetailId() != null) {
                // 更新現有記錄
                productDetailMapper.update(detailReq);
            } else {
                // 插入新記錄
                productDetailMapper.insert(detailReq);
            }
            
            // 獲取詳細響應數據
            Long productDetailId = Long.valueOf(detailReq.getProductDetailId());
            DetailRes detailRes = productDetailMapper.findById(productDetailId);
            detailResList.add(detailRes);
        }
        
        // 3. 重新計算並生成完整的獎品編號系統
        regeneratePrizeNumbers(productId);
        
        return detailResList;
        
    } catch (Exception e) {
        // 如果出現錯誤，記錄日誌但不影響現有資料
        log.error("添加產品細節時發生錯誤: productId={}", productId, e);
        throw new RuntimeException("添加產品細節失敗", e);
    }
}

public DetailRes updateProductDetail(Long id, DetailReq productDetailReq) throws Exception {
    Product productById = productRepository.getProductById(Long.valueOf(productDetailReq.getProductId()));
    
    // 檢查商品狀態
    boolean isDrawnPrize = prizeNumberMapper.isTrue(productDetailReq.getProductId())
            .stream().anyMatch(PrizeNumber::getIsDrawn);
    boolean isProductAvailable = productById.getStatus().equals(ProductStatus.AVAILABLE);
    
    // 如果產品已抽獎或已上架，僅允許更新指定字段
    if (isDrawnPrize || isProductAvailable) {
        // 僅允許更新银币、尺寸和概率
        productDetailReq.setDescription(null);
        productDetailReq.setNote(null);
        productDetailReq.setQuantity(null);
        productDetailReq.setStockQuantity(null);
        productDetailReq.setProductName(null);
        productDetailReq.setGrade(null);
        productDetailReq.setPrice(null);
        productDetailReq.setImageUrls(null);
        productDetailReq.setSpecification(null);
        productDetailReq.setIsPrize(null);
        
        // 只更新允許的欄位，不重新生成獎品編號
        productDetailReq.setProductDetailId(Math.toIntExact(id));
        productDetailMapper.update(productDetailReq);
        
        return productDetailMapper.findById(id);
    }
    
    // 未抽獎且未上架，允許完整更新
    productDetailReq.setStockQuantity(productDetailReq.getQuantity());
    productDetailReq.setDescription(escapeTextForHtml(productDetailReq.getDescription()));
    productDetailReq.setSpecification(escapeTextForHtml(productDetailReq.getSpecification()));
    productDetailReq.setProductDetailId(Math.toIntExact(id));
    productDetailMapper.update(productDetailReq);
    
    // 重新生成獎品編號
    Integer productId = Integer.valueOf(productDetailReq.getProductId());
    regeneratePrizeNumbers(productId);
    
    return productDetailMapper.findById(id);
}

/**
 * 重新生成產品的獎品編號系統
 * @param productId 產品ID
 */
private void regeneratePrizeNumbers(Integer productId) {
    // 1. 獲取該產品的所有 product_detail
    List<DetailRes> allProductDetails = productDetailMapper.findByProductId(productId);
    
    // 2. 計算總數量（排除 grade 為 "LAST" 的項）
    int totalQuantity = 0;
    for (DetailRes detail : allProductDetails) {
        if (shouldIncludeInPrizeNumbers(detail)) {
            totalQuantity += detail.getQuantity();
        }
    }
    
    // 3. 更新產品總數量
    productRepository.updateTotalQua(totalQuantity, productId);
    
    // 4. 保存已抽中的獎品資訊（如果存在）
    Map<String, PrizeNumber> drawnPrizesMap = new HashMap<>();
    List<PrizeNumber> existingPrizes = prizeNumberMapper.findByProductId(Long.valueOf(productId));
    for (PrizeNumber prize : existingPrizes) {
        if (prize.getIsDrawn()) {
            // 用 productDetailId + level 作為 key 來保存已抽中的獎品
            String key = prize.getProductDetailId() + "_" + prize.getLevel();
            drawnPrizesMap.put(key, prize);
        }
    }
    
    // 5. 删除當前產品下所有的獎品編號
    prizeNumberMapper.deleteProductById(Long.valueOf(productId));
    
    // 6. 重新生成獎品編號（按順序）
    List<PrizeNumber> newPrizeNumbers = new ArrayList<>();
    int currentNumber = 1; // 獎券編號從1開始
    
    for (DetailRes detail : allProductDetails) {
        if (!shouldIncludeInPrizeNumbers(detail)) {
            continue; // 跳過 "LAST" 等級的項目
        }
        
        // 為每個商品細節分配連續的獎券編號
        for (int i = 0; i < detail.getQuantity(); i++) {
            PrizeNumber prizeNumber = new PrizeNumber();
            prizeNumber.setProductId(productId);
            prizeNumber.setProductDetailId(detail.getProductDetailId().intValue());
            prizeNumber.setNumber(String.valueOf(currentNumber));
            prizeNumber.setLevel(detail.getGrade());
            
            // 檢查這個獎品是否之前已被抽中
            String key = detail.getProductDetailId().intValue() + "_" + detail.getGrade();
            if (drawnPrizesMap.containsKey(key) && drawnPrizesMap.get(key) != null) {
                prizeNumber.setIsDrawn(true);
                drawnPrizesMap.remove(key); // 移除已處理的項目
            } else {
                prizeNumber.setIsDrawn(false);
            }
            
            newPrizeNumbers.add(prizeNumber);
            currentNumber++;
        }
    }
    
    // 7. 批量插入新的獎品編號
    if (!newPrizeNumbers.isEmpty()) {
        prizeNumberMapper.insertBatch(newPrizeNumbers);
    }
    
    // 8. 記錄日誌
    log.info("產品 {} 重新生成獎品編號完成，總計 {} 張獎券", productId, totalQuantity);
}

/**
 * 檢查是否需要為該項目生成獎品編號
 */
private boolean shouldIncludeInPrizeNumbers(DetailRes detailRes) {
    return !"LAST".equals(detailRes.getGrade());
}

private boolean shouldIncludeInPrizeNumbers(DetailReq detailReq) {
    return !"LAST".equals(detailReq.getGrade());
}

private boolean shouldIncludeInPrizeNumbers(ProductDetail detailReq) {
    return !"LAST".equals(detailReq.getGrade());
}

/**
 * 檢查產品是否可以修改獎品編號
 */
private boolean canModifyPrizeNumbers(Integer productId) {
    // 檢查是否有已抽中的獎品
    boolean hasDrawnPrizes = prizeNumberMapper.isTrue(String.valueOf(productId))
            .stream().anyMatch(PrizeNumber::getIsDrawn);
    
    // 檢查產品是否已上架
    Product product = productRepository.getProductById(Long.valueOf(productId));
    boolean isAvailable = ProductStatus.AVAILABLE.equals(product.getStatus());
    
    return !hasDrawnPrizes && !isAvailable;
}

package com.one.onekuji.service;

import com.one.onekuji.dto.ProductDetailDTO;
import com.one.onekuji.eenum.ProductStatus;
import com.one.onekuji.model.PrizeNumber;
import com.one.onekuji.model.Product;
import com.one.onekuji.model.ProductDetail;
import com.one.onekuji.repository.PrizeNumberMapper;
import com.one.onekuji.repository.ProductDetailRepository;
import com.one.onekuji.repository.ProductRepository;
import com.one.onekuji.request.DetailReq;
import com.one.onekuji.response.DetailRes;
import com.one.onekuji.response.ProductDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PrizeNumberMapper prizeNumberMapper;

    public List<DetailRes> getAllProductDetails() {
        return productDetailMapper.findAll();
    }

    /**
     * 添加产品细节，并生成奖品编号。
     * @param detailReqs 包含产品详细信息的请求列表。
     * @return 返回插入后的详细响应列表。
     */

    public List<DetailRes> addProductDetails(List<DetailReq> detailReqs) {
        List<DetailRes> detailResList = new ArrayList<>();
        List<PrizeNumber> allPrizeNumbers = new ArrayList<>();

        Integer productId = Integer.valueOf(detailReqs.get(0).getProductId());

        // 1. 獲取該商品所有現有的 product_detail（排除本次要更新的）
        List<Integer> currentDetailIds = detailReqs.stream()
                .map(DetailReq::getProductDetailId)
                .filter(Objects::nonNull) // 過濾掉新增的項目（ID為null）
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        List<DetailRes> existingDetails = new ArrayList<>();
        if (!currentDetailIds.isEmpty()) {
            existingDetails = productDetailMapper.findByProductIdExcluding(productId, currentDetailIds);
        } else {
            // 如果都是新增的項目，則獲取該商品的所有現有記錄
            existingDetails = productDetailMapper.findByProductId(productId);
        }

        // 2. 計算總數量：現有的 + 本次傳入的（排除 grade 為 "LAST" 的項）
        int totalQuantity = 0;

        // 現有記錄的數量
        for (DetailRes existing : existingDetails) {
            if (shouldIncludeInPrizeNumbers(existing)) {
                totalQuantity += existing.getQuantity();
            }
        }

        // 本次傳入的數量
        for (DetailReq detailReq : detailReqs) {
            if (shouldIncludeInPrizeNumbers(detailReq)) {
                totalQuantity += detailReq.getQuantity();
            }
        }

        // 3. 更新產品總數量
        productRepository.updateTotalQua(totalQuantity, productId);

        // 4. 删除當前產品下所有的獎品編號，避免重複
        prizeNumberMapper.deleteProductById(Long.valueOf(productId));

        // 5. 創建並打亂獎品編號
        List<Integer> shuffledNumbers = new ArrayList<>();
        for (int i = 1; i <= totalQuantity; i++) {
            shuffledNumbers.add(i);
        }
        Collections.shuffle(shuffledNumbers); // 打亂獎品編號

        int currentIndex = 0;

        // 6. 處理本次傳入的 detailReqs
        for (DetailReq detailReq : detailReqs) {
            // 轉義 HTML 字符，確保安全
            detailReq.setDescription(escapeTextForHtml(detailReq.getDescription()));
            detailReq.setSpecification(escapeTextForHtml(detailReq.getSpecification()));
            detailReq.setStockQuantity(detailReq.getQuantity());
            detailReq.setSize(detailReq.getSize());

            if(detailReq.getIsPrize() == null){
                detailReq.setIsPrize(String.valueOf(false));
            }

            // 插入或更新產品細節
            if (detailReq.getProductDetailId() != null) {
                // 更新現有記錄
                productDetailMapper.update(detailReq);
            } else {
                // 插入新記錄
                productDetailMapper.insert(detailReq);
            }

            Long productDetailId = Long.valueOf(detailReq.getProductDetailId());

            // 為本次傳入的項目創建獎品編號（排除 "LAST" 等級）
            if (shouldIncludeInPrizeNumbers(detailReq)) {
                List<PrizeNumber> detailPrizeNumbers = new ArrayList<>();
                for (int i = 0; i < detailReq.getQuantity(); i++) {
                    PrizeNumber prizeNumber = new PrizeNumber();
                    prizeNumber.setProductId(productId);
                    prizeNumber.setProductDetailId(Math.toIntExact(productDetailId));
                    prizeNumber.setNumber(String.valueOf(shuffledNumbers.get(currentIndex)));
                    prizeNumber.setIsDrawn(false);
                    prizeNumber.setLevel(detailReq.getGrade());
                    detailPrizeNumbers.add(prizeNumber);
                    currentIndex++;
                }
                Collections.shuffle(detailPrizeNumbers);
                allPrizeNumbers.addAll(detailPrizeNumbers);
            }

            // 獲取並添加詳細響應數據
            DetailRes detailRes = productDetailMapper.findById(productDetailId);
            detailResList.add(detailRes);
        }

        // 7. 處理現有的 product_detail 記錄（為它們重新創建獎品編號）
        for (DetailRes existing : existingDetails) {
            if (shouldIncludeInPrizeNumbers(existing)) {
                List<PrizeNumber> existingPrizeNumbers = new ArrayList<>();
                for (int i = 0; i < existing.getQuantity(); i++) {
                    PrizeNumber prizeNumber = new PrizeNumber();
                    prizeNumber.setProductId(productId);
                    prizeNumber.setProductDetailId(existing.getProductDetailId().intValue());
                    prizeNumber.setNumber(String.valueOf(shuffledNumbers.get(currentIndex)));
                    prizeNumber.setIsDrawn(false);
                    prizeNumber.setLevel(existing.getGrade());
                    existingPrizeNumbers.add(prizeNumber);
                    currentIndex++;
                }
                Collections.shuffle(existingPrizeNumbers);
                allPrizeNumbers.addAll(existingPrizeNumbers);
            }

            // 將現有記錄也加入到返回列表中
            detailResList.add(existing);
        }

        // 8. 批量插入所有獎品編號
        if (!allPrizeNumbers.isEmpty()) {
            prizeNumberMapper.insertBatch(allPrizeNumbers);
        }

        // 9. 返回生成的詳細響應列表
        return detailResList;
    }
    // 輔助方法：判斷 DetailRes 是否應該包含在獎品編號中
    private boolean shouldIncludeInPrizeNumbers(DetailRes detailRes) {
        return !"LAST".equals(detailRes.getGrade());
    }
    // 判断是否应将该项包括在奖品编号中
    private boolean shouldIncludeInPrizeNumbers(DetailReq detailReq) {
        return !"LAST".equals(detailReq.getGrade());
    }


    public DetailRes updateProductDetail(Long id, DetailReq productDetailReq) throws Exception {

        // 获取产品信息
        Product productById = productRepository.getProductById(Long.valueOf(productDetailReq.getProductId()));
        DetailRes byId = productDetailMapper.findById(id);

        // 检查商品状态
        boolean isDrawnPrize = prizeNumberMapper.isTrue(productDetailReq.getProductId())
                .stream().anyMatch(PrizeNumber::getIsDrawn);
        boolean isProductAvailable = productById.getStatus().equals(ProductStatus.AVAILABLE);

        // 如果产品已抽奖或已上架，仅允许更新指定字段
        if (isDrawnPrize || isProductAvailable) {
            // 仅允许更新银币、尺寸和概率
            productDetailReq.setDescription(null);    // 禁止更新描述
            productDetailReq.setNote(null);           // 禁止更新备注
            productDetailReq.setQuantity(null);       // 禁止更新数量
            productDetailReq.setStockQuantity(null);  // 禁止更新库存
            productDetailReq.setProductName(null);    // 禁止更新商品名称
            productDetailReq.setGrade(null);          // 禁止更新等级
            productDetailReq.setPrice(null);          // 禁止更新价格
            productDetailReq.setImageUrls(null);      // 禁止更新图片链接
            productDetailReq.setSpecification(null);  // 禁止更新规格
            productDetailReq.setIsPrize(null);        // 禁止更新是否奖品
        } else {
            // 未抽奖且未上架，允许完整更新
            productDetailReq.setStockQuantity(productDetailReq.getQuantity());

            // 转义 HTML 字符
            productDetailReq.setDescription(escapeTextForHtml(productDetailReq.getDescription()));
            productDetailReq.setSpecification(escapeTextForHtml(productDetailReq.getSpecification()));
        }

        // 更新商品细节
        productDetailReq.setProductDetailId(Math.toIntExact(id));
        productDetailMapper.update(productDetailReq);

        // 如果未抽奖且未上架，则更新库存和奖品编号
        if (!isDrawnPrize && !isProductAvailable) {
            // **修復：使用統一的 DetailRes 類型獲取產品詳情**
            Integer productId = Integer.valueOf(productDetailReq.getProductId());
            List<DetailRes> productDetails = productDetailMapper.findByProductId(productId);

            int totalQuantity = 0;

            // 计算产品总数量，排除 grade 为 "LAST" 的项
            for (DetailRes detail : productDetails) {
                if (shouldIncludeInPrizeNumbers(detail)) {
                    totalQuantity += detail.getQuantity();
                }
            }

            // 更新产品总数量
            productRepository.updateTotalQua(totalQuantity, productDetailReq.getProductId());

            // 删除当前产品下所有的奖品编号
            prizeNumberMapper.deleteProductById(Long.valueOf(productDetailReq.getProductId()));

            // 重新生成奖品编号
            List<PrizeNumber> allPrizeNumbers = new ArrayList<>();
            int currentIndex = 0;

            // 创建并打乱奖品编号
            List<Integer> shuffledNumbers = new ArrayList<>();
            for (int i = 1; i <= totalQuantity; i++) {
                shuffledNumbers.add(i);
            }
            Collections.shuffle(shuffledNumbers);

            // 为每个产品细节重新生成奖品编号
            for (DetailRes detail : productDetails) {
                if (!shouldIncludeInPrizeNumbers(detail)) {
                    continue; // 跳过 "LAST" 的项
                }

                List<PrizeNumber> detailPrizeNumbers = new ArrayList<>();
                for (int i = 0; i < detail.getQuantity(); i++) {
                    PrizeNumber prizeNumber = new PrizeNumber();
                    prizeNumber.setProductId(detail.getProductId()); // 確保類型正確
                    prizeNumber.setProductDetailId(detail.getProductDetailId().intValue());
                    prizeNumber.setNumber(String.valueOf(shuffledNumbers.get(currentIndex)));
                    prizeNumber.setIsDrawn(false);
                    prizeNumber.setLevel(detail.getGrade());
                    detailPrizeNumbers.add(prizeNumber);
                    currentIndex++;
                }

                // 打乱当前产品细节的奖品编号
                Collections.shuffle(detailPrizeNumbers);
                allPrizeNumbers.addAll(detailPrizeNumbers);
            }

            // 批量插入新的奖品编号
            if (!allPrizeNumbers.isEmpty()) {
                prizeNumberMapper.insertBatch(allPrizeNumbers);
            }
        }

        // 返回更新后的商品详情
        return productDetailMapper.findById(id);
    }

    // 判断是否应将该项包括在奖品编号中
    private boolean shouldIncludeInPrizeNumbers(ProductDetail detailReq) {
        return !"LAST".equals(detailReq.getGrade());
    }


    public boolean deleteProductDetail(Long id) {
        int deleted = productDetailMapper.delete(id);
        return deleted > 0;
    }

    private DetailRes convertToEntity(DetailReq detailReq) {
        return new DetailRes(
                detailReq.getProductDetailId(),
                detailReq.getProductId(),
                escapeTextForHtml(detailReq.getDescription()), // Escape HTML in description
                detailReq.getNote(),
                detailReq.getSize(),
                detailReq.getQuantity(),
                detailReq.getStockQuantity(),
                detailReq.getProductName(),
                detailReq.getGrade(),
                detailReq.getPrice(),
                detailReq.getSliverPrice(),
                detailReq.getImageUrls(),
                detailReq.getLength(),
                detailReq.getWidth(),
                detailReq.getHeight(),
                escapeTextForHtml(detailReq.getSpecification()),
                detailReq.getProbability(),
                detailReq.getIsPrize()
        );
    }

    private String escapeTextForHtml(String text) {
        if (text == null) return "";
        // 先保留換行符的處理
        String escapedText = text.replace("\n", "<br/>").replace("\r", "");

        // 再進行其他字符轉義
        return escapedText.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                // 恢復 <br/> 原來的形式，避免被轉義
                .replace("&lt;br/&gt;", "<br/>");
    }

    public DetailRes getAllProductDetailsByProductId(Integer productId) {
        return productDetailMapper.getAllProductDetailsByProductId(productId);
    }

    public ProductDetailRes getProductById(Long productId) {
        return productDetailMapper.getProductById(productId);
    }


    public DetailRes updateProductDTO(Long id , ProductDetailDTO productDetailDTO){
        DetailRes byId = productDetailMapper.findById(id);
        byId.setSliverPrice(productDetailDTO.getSliverPrice());
        byId.setProbability(productDetailDTO.getProbability());
        byId.setSize(productDetailDTO.getSize());
        return productDetailMapper.updateProductDTO(byId);
    }
}

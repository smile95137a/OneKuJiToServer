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
        int totalQuantity = 0;

        // 计算总数量，排除 grade 为 "LAST" 的项
        // 1. 计算总数量，排除等级为 "LAST" 的项。
        for (DetailReq detailReq : detailReqs) {
                if (shouldIncludeInPrizeNumbers(detailReq)) { // 判断是否要包含在奖品编号列表中
                    totalQuantity += detailReq.getQuantity();
                }
            }

        // 2. 更新产品总数量
        productRepository.updateTotalQua(totalQuantity, detailReqs.get(0).getProductId());

        // 3. 删除当前产品下所有的奖品编号，避免重复
        prizeNumberMapper.deleteProductById(Long.valueOf(detailReqs.get(0).getProductId()));

        // 4. 创建并打乱奖品编号
        List<Integer> shuffledNumbers = new ArrayList<>();
        for (int i = 1; i <= totalQuantity; i++) {
            shuffledNumbers.add(i);
        }
        Collections.shuffle(shuffledNumbers); // 打乱奖品编号

        int currentIndex = 0;
        for (DetailReq detailReq : detailReqs) {
            // 5. 转义 HTML 字符，确保安全
            detailReq.setDescription(escapeTextForHtml(detailReq.getDescription()));
            detailReq.setSpecification(escapeTextForHtml(detailReq.getSpecification()));
            detailReq.setStockQuantity(detailReq.getQuantity());

            // 6. 计算尺寸，并将结果存入 detailReq
            detailReq.setSize(detailReq.getSize());

            if(detailReq.getIsPrize() == null){
                detailReq.setIsPrize(String.valueOf(false));
            }
            // 7. 插入产品细节，不排除 "LAST" 等级
            productDetailMapper.insert(detailReq);
            Long productDetailId = Long.valueOf(detailReq.getProductDetailId());

            // 8. 排除等级为 "LAST" 的项，不进行奖品编号处理
            if (shouldIncludeInPrizeNumbers(detailReq)) {
                List<PrizeNumber> detailPrizeNumbers = new ArrayList<>();
                // 为每个数量创建奖品编号
                for (int i = 0; i < detailReq.getQuantity(); i++) {
                    PrizeNumber prizeNumber = new PrizeNumber();
                    prizeNumber.setProductId(detailReq.getProductId());
                    prizeNumber.setProductDetailId(Math.toIntExact(productDetailId));
                    prizeNumber.setNumber(String.valueOf(shuffledNumbers.get(currentIndex)));
                    prizeNumber.setIsDrawn(false);
                    prizeNumber.setLevel(detailReq.getGrade());
                    detailPrizeNumbers.add(prizeNumber);
                    currentIndex++;
                }

                // 打乱当前产品细节的奖品编号
                Collections.shuffle(detailPrizeNumbers);
                allPrizeNumbers.addAll(detailPrizeNumbers);
            }

            // 9. 获取并添加详细响应数据
            DetailRes detailRes = productDetailMapper.findById(productDetailId);
            detailResList.add(detailRes);
        }

        // 10. 批量插入所有奖品编号，确保插入不包括 "LAST" 等级的奖品
        if (!allPrizeNumbers.isEmpty()) {
            prizeNumberMapper.insertBatch(allPrizeNumbers);
        }

        // 11. 返回生成的详细响应列表
        return detailResList;
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
            // 更新产品库存
            List<ProductDetail> productDetails = productDetailMapper.getProductDetailByProductId(Long.valueOf(productDetailReq.getProductId()));
            int totalQuantity = 0;

            // 计算产品总数量，排除 grade 为 "LAST" 的项
            for (ProductDetail detail : productDetails) {
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
            for (ProductDetail detail : productDetails) {
                if (!shouldIncludeInPrizeNumbers(detail)) {
                    continue; // 跳过 "LAST" 的项
                }

                List<PrizeNumber> detailPrizeNumbers = new ArrayList<>();
                for (int i = 0; i < detail.getQuantity(); i++) {
                    PrizeNumber prizeNumber = new PrizeNumber();
                    prizeNumber.setProductId(detail.getProductId());
                    prizeNumber.setProductDetailId(Math.toIntExact(detail.getProductDetailId()));
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

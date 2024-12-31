package com.one.service;

import com.one.repository.StoreProductRepository;
import com.one.response.StoreProductRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreProductService {

	private final StoreProductRepository storeProductRepository;

	public List<StoreProductRes> getAllStoreProducts(int page, int size) {
		int offset = page * size;
		return storeProductRepository.findAll(offset, size);
	}

	public StoreProductRes getStoreProductByProductCode(String productCode) {
		var res = storeProductRepository.findByProductCodeWithFavorites(productCode);
		
		var keywordList = storeProductRepository.findKeywordsByProductId(res.getStoreProductId());
		res.setKeywordList(keywordList);
		return res;
	}

	public boolean updateProductPopularity(String productCode) {
		try {
			storeProductRepository.incrementPopularityByProductCode(productCode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean toggleFavorite(Long userId, String productCode) {
		try {
			// 通過 productCode 獲取 storeProductId
			Long storeProductId = storeProductRepository.findStoreProductIdByProductCode(productCode);

			if (storeProductId == null) {
				// 如果找不到該產品，返回失敗
				return false;
			}

			// 檢查用戶是否已經收藏
			boolean isFavorite = storeProductRepository.isFavorite(userId, storeProductId);
			if (isFavorite) {
				// 如果已收藏，則取消收藏
				storeProductRepository.removeFavorite(userId, storeProductId);
			} else {
				// 如果未收藏，則添加收藏
				storeProductRepository.addFavorite(userId, storeProductId);
			}
			return !isFavorite;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isProductFavoritedByUser(Long userId, String productCode) {
		Long storeProductId = storeProductRepository.findStoreProductIdByProductCode(productCode);

		if (storeProductId == null) {
			// 如果找不到該產品，返回失敗
			return false;
		}

		boolean isFavorite = storeProductRepository.isFavorite(userId, storeProductId);
		return isFavorite;
	}

}

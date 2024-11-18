package com.one.frontend.repository;

import com.one.frontend.model.StoreKeyword;
import com.one.frontend.model.StoreProduct;
import com.one.frontend.response.StoreProductRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoreProductRepository {

	@Select("SELECT * FROM store_product order by created_at desc LIMIT #{size} OFFSET #{offset} ")
	List<StoreProductRes> findAll(@Param("offset") int offset, @Param("size") int size);

	@Select("SELECT * FROM store_product WHERE store_product_id = #{storeProductId}")
	StoreProductRes findResById(@Param("storeProductId") Long storeProductId);

	@Select("SELECT * FROM store_product WHERE store_product_id = #{storeProductId}")
	StoreProduct findById(@Param("storeProductId") Long storeProductId);

	@Select("SELECT * FROM store_product WHERE product_code = #{productCode}")
	StoreProduct findByProductCode(@Param("productCode") String productCode);

	@Insert("INSERT INTO store_product (product_code, product_name, description, details, specification, price, special_price, is_special_price, stock_quantity, sold_quantity, image_urls, category_id, status, shipping_method, shipping_price, size, length, width, height, popularity, created_at, updated_at, created_user_id, update_user_id) "
			+ "VALUES (#{productCode}, #{productName}, #{description}, #{details}, #{specification}, #{price}, #{specialPrice}, #{isSpecialPrice}, #{stockQuantity}, #{soldQuantity}, #{imageUrls}, #{categoryId}, #{status}, #{shippingMethod}, #{shippingPrice}, #{size}, #{length}, #{width}, #{height}, #{popularity}, #{createdAt}, #{updatedAt}, #{createdUserId}, #{updateUserId})")
	void save(StoreProduct storeProduct);

	@Update("UPDATE store_product SET product_code = #{productCode}, product_name = #{productName}, description = #{description}, details = #{details}, specification = #{specification}, price = #{price}, special_price = #{specialPrice}, is_special_price = #{isSpecialPrice}, stock_quantity = #{stockQuantity}, sold_quantity = #{soldQuantity}, image_urls = #{imageUrls}, category_id = #{categoryId}, status = #{status}, shipping_method = #{shippingMethod}, shipping_price = #{shippingPrice}, size = #{size}, length = #{length}, width = #{width}, height = #{height}, popularity = #{popularity}, created_at = #{createdAt}, updated_at = #{updatedAt}, created_user_id = #{createdUserId}, update_user_id = #{updateUserId} "
			+ "WHERE store_product_id = #{storeProductId}")
	void update(StoreProduct storeProduct);

	@Update("UPDATE store_product SET popularity = popularity + 1 WHERE store_product_id = #{storeProductId}")
	void incrementPopularity(@Param("storeProductId") Long storeProductId);

	@Select("SELECT a.*, " + "COUNT(b.user_id) AS favoritesCount, " + "c.category_name AS categoryName "
			+ "FROM store_product a "
			+ "LEFT JOIN user_favorite_store_products b ON a.store_product_id = b.store_product_id "
			+ "LEFT JOIN store_category c ON a.category_id = c.category_id " + "WHERE a.product_code = #{productCode} "
			+ "GROUP BY a.store_product_id")
	StoreProductRes findByProductCodeWithFavorites(@Param("productCode") String productCode);

	@Update("UPDATE store_product SET popularity = popularity + 1 WHERE product_code = #{productCode}")
	void incrementPopularityByProductCode(@Param("productCode") String productCode);

	@Select("SELECT COUNT(1) > 0 FROM user_favorite_store_products WHERE user_id = #{userId} AND store_product_id = #{storeProductId}")
	boolean isFavorite(@Param("userId") Long userId, @Param("storeProductId") Long storeProductId);

	@Select("SELECT store_product_id FROM store_product WHERE product_code = #{productCode}")
	Long findStoreProductIdByProductCode(@Param("productCode") String productCode);

	@Insert("INSERT INTO user_favorite_store_products (user_id, store_product_id) "
			+ "VALUES (#{userId}, #{storeProductId})")
	void addFavorite(@Param("userId") Long userId, @Param("storeProductId") Long storeProductId);

	@Delete("DELETE FROM user_favorite_store_products WHERE user_id = #{userId} AND store_product_id = #{storeProductId}")
	void removeFavorite(@Param("userId") Long userId, @Param("storeProductId") Long storeProductId);

	@Insert("INSERT INTO store_product_keywords (store_product_id, keyword_id) "
			+ "VALUES (#{storeProductId}, #{keywordId})")
	void addStoreProductKeyword(@Param("storeProductId") Long storeProductId, @Param("keywordId") Long keywordId);

	 @Insert("INSERT INTO store_product_keywords (store_product_id, keyword_id) " +
	            "VALUES (#{storeProductId}, #{keywordId})")
	    void insertProductKeyword(@Param("storeProductId") Long storeProductId, @Param("keywordId") Long keywordId);

    @Select("SELECT k.* " +
            "FROM store_product_keywords pk " +
            "JOIN store_keyword k ON pk.keyword_id = k.id " +
            "WHERE pk.store_product_id = #{storeProductId}")
    List<StoreKeyword> findKeywordsByProductId(@Param("storeProductId") Long storeProductId);


	@Update("""
    UPDATE store_product 
    SET 
        stock_quantity = #{stockQuantity}, 
        sold_quantity = #{soldQuantity}, 
        status = #{status} 
    WHERE store_product_id = #{storeProductId}
""")
	void updateStoreProduct(
			@Param("storeProductId") Long storeProductId,
			@Param("stockQuantity") int stockQuantity,
			@Param("soldQuantity") int soldQuantity,
			@Param("status") String status
	);
}

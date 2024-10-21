package com.one.frontend.service;

import com.one.frontend.dto.OrderDetailDto;
import com.one.frontend.eenum.OrderStatus;
import com.one.frontend.eenum.PrizeCategory;
import com.one.frontend.eenum.ProductType;
import com.one.frontend.model.*;
import com.one.frontend.repository.*;
import com.one.frontend.response.DrawResponse;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
import com.one.frontend.response.UserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawResultService {

	private final DrawRepository drawRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final ProductDetailRepository productDetailRepository;
	private final PrizeNumberMapper prizeNumberMapper;
	private final OrderService orderService;
	private final PrizeCartItemRepository prizeCartItemRepository;
	private final PrizeCartRepository prizeCartRepository;
	@Autowired
	private UserTransactionRepository userTransactionRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;


	// 本地锁，用于锁定抽奖资源
	private final ConcurrentHashMap<Long, Lock> productLockMap = new ConcurrentHashMap<>();
	// 存储用户的锁
	private final Map<Long, Lock> userLockMap = new ConcurrentHashMap<>();
	// 存储产品的上次抽奖时间和抽奖用户
	private final Map<Long, DrawProtection> productDrawProtectionMap = new ConcurrentHashMap<>();

	private static class DrawProtection {
		LocalDateTime lastDrawTime;
		Long userId;

		DrawProtection(LocalDateTime lastDrawTime, Long userId) {
			this.lastDrawTime = lastDrawTime;
			this.userId = userId;
		}
	}

	private Lock getLockForUser(Long userId) {
		return userLockMap.computeIfAbsent(userId, k -> new ReentrantLock());
	}

	// 获取抽奖保护时间，根据抽奖次数计算保护期时间
	private long getDrawProtectionTime(int drawCount) {
		long protectionTime = 300 + (30 * (drawCount - 1)); // 初始保护时间300秒，每多抽一次加30秒
		return Math.min(protectionTime, 600); // 最大保护时间为600秒
	}

	// 抽奖操作，处理锁机制和保护期
	public LocalDateTime handleDrawForLock(Long userId, Long productId, List<String> prizeNumbers) throws Exception {
		Lock lock = getLockForUser(userId); // 获取用户锁
		if (lock.tryLock()) { // 尝试获取锁
			try {
				LocalDateTime now = LocalDateTime.now(); // 当前时间
				DrawProtection protection = productDrawProtectionMap.get(productId); // 获取该产品的保护信息
				long protectionTime = getDrawProtectionTime(prizeNumbers.size()); // 根据抽奖次数计算保护时间

				// 如果有保护信息，计算是否在保护期内
				if (protection != null) {
					long secondsSinceLastDraw = Duration.between(protection.lastDrawTime, now).getSeconds();
					System.out.println("Protection time: " + protectionTime + " seconds");
					System.out.println("Seconds since last draw: " + secondsSinceLastDraw + " seconds");

					// 如果在保护期内，且不是同一个用户，则抛出异常
					if (secondsSinceLastDraw < protectionTime && !Objects.equals(userId, protection.userId)) {
						throw new Exception("抽獎保護期內，其他用戶不能抽獎。剩餘時間：" + (protectionTime - secondsSinceLastDraw) + "秒");
					}
				}

				// 更新抽奖保护信息，设置新的保护时间
				productDrawProtectionMap.put(productId, new DrawProtection(now, userId));
				System.out.println("Updated DrawProtection for productId: " + productId + ", userId: " + userId);

				// 返回保护期的结束时间
				LocalDateTime endTimes = now.plusSeconds(protectionTime);
				return endTimes;
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("抽獎發生錯誤: " + e.getMessage());
			} finally {
				lock.unlock(); // 释放锁
			}
		} else {
			throw new Exception("目前有其他用戶正在抽獎，請稍後。");
		}
	}






	public List<DrawResult> handleDraw(Long userId, Long productId) throws Exception {
		Random random = new Random();
		List<Long> number = prizeNumberMapper.getNumbers(productId);
		int randomIndex = random.nextInt(number.size());
		String prizeNumber = number.get(randomIndex).toString();
		List<String> prizeNumbers = new ArrayList<>();
		prizeNumbers.add(prizeNumber);
        return handleDraw2(userId , productId , prizeNumbers , "1");
	}

	public DrawResponse getAllPrizes(Long productId, Long userId) {
		// 1. 获取产品详细信息
		List<ProductDetail> productDetails = productDetailRepository.getAllProductDetailsByProductId(productId);
		List<PrizeNumber> allPrizeNumbers = new ArrayList<>();

		// 2. 获取奖品编号
		for (ProductDetail productDetail : productDetails) {
			List<PrizeNumber> prizeNumbers = prizeNumberMapper
					.getAllPrizeNumbersByProductDetailId(Long.valueOf(productDetail.getProductDetailId()));
			for (PrizeNumber prize : prizeNumbers) {
				if (!prize.getIsDrawn()) {
					prize.setLevel(null);  // 只显示未抽中的奖品
				}
			}
			allPrizeNumbers.addAll(prizeNumbers);
		}

		// 3. 对 allPrizeNumbers 按照 number 排序
		allPrizeNumbers.sort(Comparator.comparing(PrizeNumber::getNumber)); // 假设 PrizeNumber 有 getNumber() 方法

		// 4. 获取当前时间
		LocalDateTime now = LocalDateTime.now();

		// 5. 获取当前产品的抽奖保护状态
		DrawProtection protection = productDrawProtectionMap.get(productId);
		LocalDateTime endTimes = null; // 初始化 endTimes 为 null

		// 6. 如果有保护状态且与当前用户无关，计算保护时间
		if (protection != null) {
			long secondsSinceLastDraw = Duration.between(protection.lastDrawTime, now).getSeconds();
			int drawCount = (int) allPrizeNumbers.stream().filter(PrizeNumber::getIsDrawn).count();
			long protectionTime = getDrawProtectionTime(drawCount);

			// 判断是否在保护期内
			if (secondsSinceLastDraw < protectionTime) {
				endTimes = protection.lastDrawTime.plusSeconds(protectionTime);
			}
		}

		allPrizeNumbers.sort(Comparator.comparing(prize -> {
			try {
				return Long.parseLong(prize.getNumber()); // 假设 number 是可以转换为 Long 类型的字符串
			} catch (NumberFormatException e) {
				// 如果 number 无法转换为 Long，则返回一个非常大的值，避免影响排序
				return Long.MAX_VALUE;
			}
		}));

		// 7. 返回结果，包括排序后的奖品列表和保护结束时间
		return new DrawResponse(allPrizeNumbers, endTimes);
	}



	public List<DrawResult> handleDraw2(Long userId, Long productId, List<String> prizeNumbers, String payMethod) throws Exception {
		try {
			// 验证用户
			Long prizeCartId = prizeCartRepository.getCartIdByUserId(userId);
			if (prizeCartId == null) {
				throw new Exception("沒有賞品盒不能抽獎，請聯繫客服人員");
			}

			// 获取产品详细信息
			ProductRes product = productRepository.getProductById(productId);
			if (product == null) {
				throw new Exception("產品不存在");
			}

			List<ProductDetailRes> productDetails = productDetailRepository.getProductDetailByProductId(productId);
			if (productDetails.isEmpty()) {
				throw new Exception("該產品沒有可用的獎品");
			}

			// 获取选中的奖品编号
			List<PrizeNumber> selectedPrizeNumbers = prizeNumberMapper.getPrizeNumbersByProductIdAndNumbers(productId, prizeNumbers);
			if (selectedPrizeNumbers.size() != prizeNumbers.size()) {
				throw new Exception("部分指定的獎品編號不存在或重複");
			}

			// 验证并扣除用户余额
			BigDecimal totalAmount = calculateTotalAmount(product, prizeNumbers.size(), payMethod);
			deductUserBalance(userId, totalAmount, product.getPrizeCategory(), payMethod);

			// **记录消费** (在扣除用户余额后)
			recordConsume(userId, totalAmount);

			// 为每个选择的编号随机抽奖（按概率）
			List<PrizeNumber> drawnPrizeNumbers = drawPrizesForNumbersWithStock(selectedPrizeNumbers, productDetails);

			// 处理抽奖结果
			List<DrawResult> drawResults = processDrawResults(userId, productId, drawnPrizeNumbers, product, payMethod);

			// 更新奖品库存和状态
			updateProductStock(productId, drawnPrizeNumbers);

			// 添加商品到奖品盒
			addToPrizeCart(prizeCartId, drawnPrizeNumbers, product);

			// 处理SP奖并获取 DrawResult
			DrawResult spDrawResult = handleSPPrize(userId, productId, prizeCartId , product ,payMethod);
			if (spDrawResult != null) {
				drawResults.add(spDrawResult);
			}

			// 计算并更新用户红利
			updateUserBonus(userId, totalAmount, prizeNumbers.size(), payMethod);

			return drawResults;
		} catch (Exception e) {
			throw new Exception("抽獎過程中出現錯誤: " + e.getMessage());
		}
	}

	private void recordConsume(Long userId, BigDecimal amount) {
		userTransactionRepository.insertTransaction(userId, "CONSUME", amount);
	}


	private BigDecimal calculateTotalAmount(ProductRes product, int count, String payMethod) {
		BigDecimal unitPrice;
		if (product.getPrizeCategory() == PrizeCategory.BONUS) {
			unitPrice = product.getBonusPrice();
		} else if ("1".equals(payMethod)) {
			unitPrice = product.getPrice();
		} else if ("2".equals(payMethod)) {
			unitPrice = product.getSliverPrice();
		} else {
			throw new IllegalArgumentException("無效的支付方式");
		}
		return unitPrice.multiply(BigDecimal.valueOf(count));
	}

	private void deductUserBalance(Long userId, BigDecimal totalAmount, PrizeCategory category, String payMethod) throws Exception {
		if (category == PrizeCategory.BONUS) {
			BigDecimal bonusPoints = new BigDecimal(userRepository.getBonusPoints(userId));
			if (bonusPoints.compareTo(totalAmount) < 0) {
				throw new Exception("红利不足");
			}
			userRepository.deductUserBonusPoints(userId, bonusPoints.subtract(totalAmount));
		} else if ("1".equals(payMethod)) {
			BigDecimal balance = new BigDecimal(userRepository.getBalance(userId));
			if (balance.compareTo(totalAmount) < 0) {
				throw new Exception("餘額不足，請加值");
			}
			userRepository.deductUserBalance(userId, balance.subtract(totalAmount));
		} else if ("2".equals(payMethod)) {
			BigDecimal sliver = new BigDecimal(String.valueOf(userRepository.getSliver(userId)));
			if (sliver.compareTo(totalAmount) < 0) {
				throw new Exception("銀幣不足");
			}
			userRepository.deductUserSliver(userId, sliver.subtract(totalAmount));
		}
	}

	private List<DrawResult> processDrawResults(Long userId, Long productId, List<PrizeNumber> drawnPrizeNumbers,
												ProductRes product, String payMethod) {
		List<DrawResult> drawResults = new ArrayList<>();
		int totalDrawCount = drawnPrizeNumbers.size();
		int remainingDrawCount = totalDrawCount;
		UserRes user = userRepository.getUserById(userId);

		for (PrizeNumber drawnPrizeNumber : drawnPrizeNumbers) {
			prizeNumberMapper.markPrizeNumberAsDrawn(drawnPrizeNumber.getPrizeNumberId(),
					drawnPrizeNumber.getProductId(),
					drawnPrizeNumber.getProductDetailId());

			ProductDetailRes prizeDetail = productDetailRepository.getProductDetailById(drawnPrizeNumber.getProductDetailId());

			DrawResult drawResult = createDrawResult(userId, productId, prizeDetail, product, payMethod,
					drawnPrizeNumber.getNumber(), totalDrawCount, remainingDrawCount);
			drawResults.add(drawResult);

			// 发送抽奖结果消息
			sendGachaMessage(user, prizeDetail);

			remainingDrawCount--;
		}

		drawRepository.insertBatch(drawResults);
		return drawResults;
	}

	private DrawResult createDrawResult(Long userId, Long productId, ProductDetailRes prizeDetail,
										ProductRes product, String payMethod, String prizeNumber,
										int totalDrawCount, int remainingDrawCount) {
		DrawResult drawResult = new DrawResult();
		drawResult.setUserId(userId);
		drawResult.setProductId(productId);
		drawResult.setProductDetailId(prizeDetail.getProductDetailId().longValue());
		drawResult.setDrawTime(LocalDateTime.now());
		drawResult.setAmount(getAmountByPayMethod(product, payMethod));
		drawResult.setDrawCount(1);
		drawResult.setRemainingDrawCount(remainingDrawCount);
		drawResult.setPrizeNumber(prizeNumber);
		drawResult.setStatus("ACTIVE");
		drawResult.setTotalDrawCount((long) totalDrawCount);
		drawResult.setCreateDate(LocalDateTime.now());
		drawResult.setUpdateDate(LocalDateTime.now());
		drawResult.setImageUrls(prizeDetail.getImageUrls());
		drawResult.setProductName(prizeDetail.getProductName());
		return drawResult;
	}

	private BigDecimal getAmountByPayMethod(ProductRes product, String payMethod) {
		if (product.getPrizeCategory() == PrizeCategory.BONUS) {
			return product.getBonusPrice();
		} else if ("1".equals(payMethod)) {
			return product.getPrice();
		} else if ("2".equals(payMethod)) {
			return product.getSliverPrice();
		}
		throw new IllegalArgumentException("無效的支付方式");
	}

	private void sendGachaMessage(UserRes user, ProductDetailRes prizeDetail) {
		GachaMessage message = new GachaMessage();
		message.setNickName(user.getNickname());
		message.setName(prizeDetail.getProductName());
		message.setProductDetail(prizeDetail);
		message.setCreatedDate(LocalDateTime.now());
		messagingTemplate.convertAndSend("/topic/lottery", message);
	}

	private void updateProductStock(Long productId, List<PrizeNumber> drawnPrizeNumbers) {
//		for (PrizeNumber drawnPrizeNumber : drawnPrizeNumbers) {
//			ProductDetailRes prizeDetail = productDetailRepository.getProductDetailById(drawnPrizeNumber.getProductDetailId());
//			prizeDetail.setQuantity(prizeDetail.getQuantity() - 1);
//			productDetailRepository.updateProductDetailQuantity(prizeDetail);
//		}

		// 检查并更新产品状态
		List<ProductDetailRes> remainingDetails = productDetailRepository.getProductDetailByProductId(productId);
		int totalRemainingQuantity = remainingDetails.stream().mapToInt(ProductDetailRes::getQuantity).sum();
		if (totalRemainingQuantity == 0) {
			productRepository.updateStatus(productId);
		}
	}

	private void addToPrizeCart(Long prizeCartId, List<PrizeNumber> drawnPrizeNumbers, ProductRes product) {
		List<PrizeCartItem> cartItemList = new ArrayList<>();
		for (PrizeNumber drawnPrizeNumber : drawnPrizeNumbers) {
			ProductDetailRes prizeDetail = productDetailRepository.getProductDetailById(drawnPrizeNumber.getProductDetailId());
			PrizeCartItem cartItem = new PrizeCartItem();
			cartItem.setCartId(prizeCartId);
			cartItem.setSize(prizeDetail.getSize());
			cartItem.setProductDetailId(prizeDetail.getProductDetailId());
			cartItem.setIsSelected(false);
			cartItem.setQuantity(1);
			if (product.getProductType().equals(ProductType.GACHA) || product.getProductType().equals(ProductType.BLIND_BOX)) {
				cartItem.setSliverPrice(BigDecimal.ZERO);
			} else {
				cartItem.setSliverPrice(prizeDetail.getSliverPrice());
			}

			cartItemList.add(cartItem);
		}
		prizeCartItemRepository.insertBatch(cartItemList);
	}

	private DrawResult handleSPPrize(Long userId, Long productId, Long prizeCartId , ProductRes product , String payMethod) {
		DrawResult drawResult = null; // 初始为 null
		// 获取所有产品细节
		List<ProductDetailRes> productDetails = productDetailRepository.getProductDetailByProductId(productId);
		int totalQuantity = productDetails.stream().mapToInt(ProductDetailRes::getQuantity).sum();

		// 获取 "LAST" 奖品的细节
		ProductDetailRes LastPrize = productDetailRepository.getProductDetailSpPrizeByProductId(productId);

		// Debugging output
		System.out.println("Total Quantity: " + totalQuantity);
		System.out.println("Last Prize: " + LastPrize);

		// 检查条件
		if (totalQuantity == 0 && LastPrize != null) {
			PrizeCartItem spCartItem = new PrizeCartItem();
			spCartItem.setCartId(prizeCartId);
			spCartItem.setSize(LastPrize.getSize());
			spCartItem.setQuantity(1);
			spCartItem.setSliverPrice(LastPrize.getSliverPrice());
			spCartItem.setProductDetailId(LastPrize.getProductDetailId());
			spCartItem.setIsSelected(true);

			// 插入奖品项
			prizeCartItemRepository.insertBatch(Collections.singletonList(spCartItem));

			// 更新数量
			LastPrize.setQuantity(LastPrize.getQuantity() - 1);
			productDetailRepository.updateProductDetailQuantity(LastPrize);
			productRepository.updateStatus(productId);

			// Debugging output
			System.out.println("Successfully added LAST prize to cart.");

			// 填充 drawResult
			drawResult = new DrawResult();
			drawResult.setUserId(userId);
			drawResult.setProductId(productId);
			drawResult.setProductDetailId(LastPrize.getProductDetailId().longValue());
			drawResult.setDrawTime(LocalDateTime.now());
			drawResult.setAmount(BigDecimal.valueOf(0));
			drawResult.setDrawCount(0);
			drawResult.setRemainingDrawCount(0);
			drawResult.setPrizeNumber(String.valueOf(0));
			drawResult.setStatus("ACTIVE");
			drawResult.setTotalDrawCount(0L);
			drawResult.setCreateDate(LocalDateTime.now());
			drawResult.setUpdateDate(LocalDateTime.now());
			drawResult.setImageUrls(LastPrize.getImageUrls());
			drawResult.setProductName(LastPrize.getProductName());
		} else {
			// 如果没有抽到 LAST 奖品，打印出原因
			if (totalQuantity != 1) {
				System.out.println("Condition not met: Total quantity is not 1.");
			} else if (LastPrize == null) {
				System.out.println("Condition not met: Last prize does not exist.");
			} else if (LastPrize.getQuantity() <= 0) {
				System.out.println("Condition not met: Last prize quantity is zero or negative.");
			}
		}
		return drawResult; // 返回可能为 null 的 drawResult
	}

	private void updateUserBonus(Long userId, BigDecimal totalPrice, int count, String payMethod) {
		BigDecimal bonusPercentage;
		if (count >= 10) {
			bonusPercentage = new BigDecimal("0.10");
		} else if (count >= 5) {
			bonusPercentage = new BigDecimal("0.04");
		} else if (count > 3) {
			bonusPercentage = new BigDecimal("0.02");
		} else {
			bonusPercentage = BigDecimal.ZERO;
		}

		BigDecimal bonusAmount = totalPrice.multiply(bonusPercentage);
		if (bonusAmount.compareTo(BigDecimal.ZERO) > 0) {
			userRepository.updateBonus(userId, bonusAmount);
			System.out.println((payMethod.equals("1") ? "金幣" : "銀幣") + "支付方式的红利金额: " + bonusAmount);
		}
	}

	private List<PrizeNumber> drawPrizesForNumbersWithStock(List<PrizeNumber> selectedPrizeNumbers,
															List<ProductDetailRes> productDetails) {
		List<PrizeNumber> drawnPrizeNumbers = new ArrayList<>();

		for (PrizeNumber selectedPrizeNumber : selectedPrizeNumbers) {
			PrizeNumber drawnPrizeNumber;

			// 继续尝试抽奖，直到抽到非 LAST 奖品
			do {
				drawnPrizeNumber = drawPrizeForNumber(selectedPrizeNumber, productDetails);
				if (drawnPrizeNumber != null) {
					// 检查是否抽到了 LAST 奖品
					if (isLastPrize(drawnPrizeNumber)) {
						// 如果抽到了 LAST 奖品，则清空并重新尝试抽取
						drawnPrizeNumber = null; // 将其设置为 null，以继续抽奖
					}
				} else {
					// 处理所有奖品都抽完的情况
					throw new RuntimeException("所有獎品都已抽完");
				}
			} while (drawnPrizeNumber == null); // 继续抽取，直到获得有效的奖品

			drawnPrizeNumbers.add(drawnPrizeNumber);
		}

		return drawnPrizeNumbers;
	}

	// 辅助方法：判断是否为 LAST 奖品
	private boolean isLastPrize(PrizeNumber prizeNumber) {
		// 实现判断逻辑，比如通过 prizeNumber 的属性判断
		return "LAST".equals(prizeNumber.getLevel()); // 假设 "number" 是表示奖品编号的字段
	}
		/**
		 * 抽奖核心逻辑
		 * @param selectedPrizeNumber 选中的奖号
		 * @param productDetails 奖品详情列表
		 * @return 抽奖结果
		 */
		@Transactional
		public PrizeNumber drawPrizeForNumber(PrizeNumber selectedPrizeNumber, List<ProductDetailRes> productDetails) {
			Random random = new Random();
			List<ProductDetail> toUpdateProductDetails = new ArrayList<>();
			List<PrizeNumber> toUpdatePrizeNumbers = new ArrayList<>();

			while (true) {
				// 1. 過濾並排序可用獎品（按概率從小到大排序，讓小概率的後面再判斷）
				List<ProductDetailRes> availableProductDetails = productDetails.stream()
						.filter(detail -> detail.getQuantity() > 0)
						.sorted((o1, o2) -> Double.compare(o1.getProbability(), o2.getProbability()))
						.collect(Collectors.toList());

				if (availableProductDetails.isEmpty()) {
					throw new RuntimeException("獎品已抽完");
				}

				// 2. 計算總概率並進行指數平滑處理
				double totalProbability = availableProductDetails.stream()
						.mapToDouble(detail -> Math.pow(detail.getProbability(), 2)) // 使用平方處理增加差異
						.sum();

				// 3. 生成隨機數，使用更精確的範圍
				double randomNumber = random.nextDouble() * totalProbability;
				double cumulativeProbability = 0.0;

				// 4. 改進的抽獎邏輯
				boolean prizeSelected = false;
				for (ProductDetailRes detail : availableProductDetails) {
					// 使用平方處理概率，增加低概率獎品的難度
					double adjustedProbability = Math.pow(detail.getProbability(), 2);
					cumulativeProbability += adjustedProbability;

					// 額外的隨機性檢查，降低低概率獎品的中獎機會
					if (randomNumber <= cumulativeProbability) {
//						// 對於低概率獎品增加額外的隨機性檢查
//						if (detail.getProbability() < 0.5) {  // 可以調整這個閾值
//							// 再次生成隨機數，必須小於原始概率才能中獎
//							if (random.nextDouble() > detail.getProbability()) {
//								continue;  // 未通過額外檢查，跳過這個獎品
//							}
//						}

						// 5. 二次檢查庫存（防止並發）
						if (detail.getQuantity() > 0) {
							// 6. 更新中獎號碼信息
							selectedPrizeNumber.setLevel(detail.getGrade());
							selectedPrizeNumber.setIsDrawn(true);
							selectedPrizeNumber.setProductDetailId(detail.getProductDetailId());

							// 7. 更新庫存
							int newQuantity = detail.getQuantity() - 1;
							if (newQuantity >= 0) {
								detail.setQuantity(newQuantity);

								// 8. 添加到批量更新列表
								toUpdateProductDetails.add(new ProductDetail(
										detail.getProductDetailId(),
										newQuantity,
										detail.getDrawnNumbers()
								));
								toUpdatePrizeNumbers.add(selectedPrizeNumber);
								prizeSelected = true;
								break;
							}
						}
					}
				}

				// 如果沒有選中獎品，默認選擇最高概率的獎品
				if (!prizeSelected && !availableProductDetails.isEmpty()) {
					ProductDetailRes highestProbabilityPrize = availableProductDetails.get(availableProductDetails.size() - 1);
					if (highestProbabilityPrize.getQuantity() > 0) {
						selectedPrizeNumber.setLevel(highestProbabilityPrize.getGrade());
						selectedPrizeNumber.setIsDrawn(true);
						selectedPrizeNumber.setProductDetailId(highestProbabilityPrize.getProductDetailId());

						int newQuantity = highestProbabilityPrize.getQuantity() - 1;
						highestProbabilityPrize.setQuantity(newQuantity);

						toUpdateProductDetails.add(new ProductDetail(
								highestProbabilityPrize.getProductDetailId(),
								newQuantity,
								highestProbabilityPrize.getDrawnNumbers()
						));
						toUpdatePrizeNumbers.add(selectedPrizeNumber);
					}
				}

				// 9. 批量更新數據
				if (!toUpdatePrizeNumbers.isEmpty()) {
					try {
						// 更新中獎號碼
						prizeNumberMapper.updatePrizeNumberBatch(
								toUpdatePrizeNumbers,
								selectedPrizeNumber.getProductId()
						);

						// 更新獎品庫存
						if (!toUpdateProductDetails.isEmpty()) {
							productDetailRepository.updateProductDetailQuantityAndDrawnNumbersBatch(
									toUpdateProductDetails
							);
						}

						return selectedPrizeNumber;
					} catch (Exception e) {
						throw new RuntimeException("抽獎過程中發生錯誤", e);
					}
				}
			}
		}











	public DrawResult handleDrawRandom(Long userId, Long productId) throws Exception {


		// 获取未抽走的奖品编号
		List<ProductDetailRes> product = productDetailRepository.getProductDetailByProductId(productId);
		if (product.isEmpty()) {
			throw new Exception("獎品已被抽完");
		}

		Random random = new Random();

// 定義每種獎品的概率
		double grandPrizeProbability = 0.05; // 大獎的概率為5%
		double consolationPrizeProbability = 0.95; // 安慰獎的概率為95%

		// 扣除會員內儲值金
		BigDecimal totalAmount = BigDecimal.ZERO;
		ProductRes product2 = productRepository.getProductById(productId);
		BigDecimal amount = product2.getPrice();
		PrizeCategory category = product2.getPrizeCategory();
		Integer balanceInt = userRepository.getBalance(userId);
		BigDecimal balance = new BigDecimal(balanceInt);
		if (category == PrizeCategory.BONUS) {
			Integer bonusPointsInt = userRepository.getBonusPoints(userId);
			BigDecimal bonusPoints = new BigDecimal(bonusPointsInt);
			if (bonusPoints.compareTo(totalAmount) >= 0) {
				BigDecimal newBonusPoints = bonusPoints.subtract(totalAmount);
				userRepository.deductUserBonusPoints(userId, newBonusPoints);
			} else {
				throw new Exception("紅利不足");
			}
		} else {
			if (balance.compareTo(totalAmount) >= 0) {
				BigDecimal newBalance = balance.subtract(totalAmount);
				userRepository.deductUserBalance(userId, newBalance);
			} else {
				throw new Exception("餘額不足，請加值");
			}
		}

		// 根據隨機概率決定獎品
		ProductDetailRes selectedPrizeDetail;
		if (random.nextDouble() < grandPrizeProbability) {
			// 使用戶贏得大獎
					selectedPrizeDetail = getGrandPrizeDetail(productId);
		} else {
			// 使用戶贏得安慰獎
			selectedPrizeDetail = getConsolationPrizeDetail(productId);
		}

// 檢查選中的獎品是否仍可用
		if (selectedPrizeDetail.getQuantity() <= 0) {
			throw new Exception("選中的獎品已經被抽完");
		}

// 更新獎品數量
		selectedPrizeDetail.setQuantity(selectedPrizeDetail.getQuantity() - 1);
		productDetailRepository.updateProductDetailQuantity(selectedPrizeDetail);
		// Record draw result
		DrawResult drawResult = new DrawResult();
		drawResult.setUserId(Long.valueOf(userId));
		drawResult.setProductDetailId(Long.valueOf(selectedPrizeDetail.getProductDetailId()));
		drawResult.setDrawTime(LocalDateTime.now());
		drawResult.setDrawCount(1);
		drawResult.setCreateDate(LocalDateTime.now());
		drawRepository.insertDrawResult(drawResult);

		// Record order and order details
		// 生成订单
		var orderNumber = orderService.genOrderNumber();
		
		var orderEntity = Order.builder()
		.userId(userId)
		.orderNumber(orderNumber)
		.createdAt(LocalDateTime.now())
		.resultStatus(OrderStatus.PREPARING_SHIPMENT)
		.totalAmount(amount)
		.build();
		orderRepository.insertOrder(orderEntity);

		// 获取订单ID
		Long orderId = orderRepository.getOrderIdByOrderNumber(orderNumber);
		List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

		// 生成订单明细
		OrderDetailDto orderDetail = new OrderDetailDto();
		orderDetail.setOrderId(orderId);
		orderDetail.setProductId(productId);
		orderDetail.setProductDetailId(selectedPrizeDetail.getProductDetailId());
		orderDetail.setProductDetailName(selectedPrizeDetail.getProductName());
		orderDetail.setQuantity(1);
		orderDetail.setUnitPrice(amount);
		orderDetail.setResultStatus(OrderStatus.PREPARING_SHIPMENT);

		orderDetailRepository.insertOrderDetailOne(orderDetail);

		return drawResult;
	}

	// 獲取大獎詳細信息
	public ProductDetailRes getGrandPrizeDetail(Long productId) {
		return productDetailRepository.findFirstByProductIdAndPrizeType(productId, "GRAND");
	}

	// 獲取安慰獎詳細信息
	public ProductDetailRes getConsolationPrizeDetail(Long productId) {
		return productDetailRepository.findFirstByProductIdAndPrizeType(productId, "CONSOLATION");
	}

}

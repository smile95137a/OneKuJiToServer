package com.one.frontend.service;

import com.one.frontend.dto.OrderDetailDto;
import com.one.frontend.eenum.OrderStatus;
import com.one.frontend.eenum.PrizeCategory;
import com.one.frontend.model.DrawResult;
import com.one.frontend.model.Order;
import com.one.frontend.model.PrizeNumber;
import com.one.frontend.model.ProductDetail;
import com.one.frontend.repository.*;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
import com.one.frontend.response.UserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

	public List<DrawResult> handleDraw(Long userId, Long productId) throws Exception {
		Random random = new Random();
		List<Long> number = prizeNumberMapper.getNumbers(productId);
		int randomIndex = random.nextInt(number.size());
		String prizeNumber = number.get(randomIndex).toString();
		List<String> prizeNumbers = new ArrayList<>();
		prizeNumbers.add(prizeNumber);
        return handleDraw2(userId , productId , prizeNumbers);
	}

	public List<PrizeNumber> getAllPrizes(Long productId) {
		List<ProductDetail> productDetails = productDetailRepository.getAllProductDetailsByProductId(productId);

		List<PrizeNumber> allPrizeNumbers = new ArrayList<>();

		for (ProductDetail productDetail : productDetails) {
			List<PrizeNumber> prizeNumbers = prizeNumberMapper
					.getAllPrizeNumbersByProductDetailId(Long.valueOf(productDetail.getProductDetailId()));

			for (PrizeNumber prize : prizeNumbers) {
				if (!prize.getIsDrawn()) {
					prize.setLevel(null); // 如果未抽中，设置 grade 为 null
				}
			}

			allPrizeNumbers.addAll(prizeNumbers);
		}

		return allPrizeNumbers;
	}

	public List<DrawResult> handleDraw2(Long userId, Long productId, List<String> prizeNumbers) throws Exception {
		try {
			// 确认商品是否存在
			List<PrizeNumber> availablePrizeNumbers = prizeNumberMapper
					.getAvailablePrizeNumbersByProductDetailId(productId);
			if (availablePrizeNumbers.isEmpty()) {
				throw new Exception("所有奖品已被抽完");
			}

			// 查找指定的奖品编号
			List<PrizeNumber> selectedPrizeNumbers = availablePrizeNumbers.stream()
					.filter(pn -> prizeNumbers.contains(pn.getNumber()) && !pn.getIsDrawn())
					.collect(Collectors.toList());

			if (selectedPrizeNumbers.size() < prizeNumbers.size()) {
				throw new Exception("部分指定的奖品编号不存在或已被抽走");
			}

			// 获取商品价格
			ProductRes product = productRepository.getProductById(Math.toIntExact(productId));
			BigDecimal amount = product.getPrice();

			// 计算抽奖总金额
			BigDecimal totalAmount = amount.multiply(BigDecimal.valueOf(prizeNumbers.size()));

			// 扣除会员内储值金或红利
			PrizeCategory category = product.getPrizeCategory();
			BigDecimal balance = new BigDecimal(userRepository.getBalance(userId));
			if (category == PrizeCategory.BONUS) {
				BigDecimal bonusPoints = new BigDecimal(userRepository.getBonusPoints(userId));
				if (bonusPoints.compareTo(totalAmount) >= 0) {
					BigDecimal newBonusPoints = bonusPoints.subtract(totalAmount);
					userRepository.deductUserBonusPoints(userId, newBonusPoints);
				} else {
					throw new Exception("红利不足，请加值");
				}
			} else {
				if (balance.compareTo(totalAmount) >= 0) {
					BigDecimal newBalance = balance.subtract(totalAmount);
					userRepository.deductUserBalance(userId, newBalance);
				} else {
					throw new Exception("余额不足，请加值");
				}
			}

			List<DrawResult> drawResults = new ArrayList<>();
			int remainingDrawCount = prizeNumbers.size();

			// 处理每个抽中的奖品
			for (PrizeNumber selectedPrizeNumber : selectedPrizeNumbers) {
				prizeNumberMapper.markPrizeNumberAsDrawn(selectedPrizeNumber.getPrizeNumberId(), selectedPrizeNumber.getProductId(), selectedPrizeNumber.getProductDetailId());

				// 更新奖品数量
				ProductDetailRes selectedPrizeDetail = productDetailRepository
						.getProductDetailById(Math.toIntExact(selectedPrizeNumber.getProductDetailId()));
				selectedPrizeDetail.setQuantity(selectedPrizeDetail.getQuantity() - 1);
				productDetailRepository.updateProductDetailQuantity(selectedPrizeDetail);

				// 记录抽奖结果
				DrawResult drawResult = new DrawResult();
				drawResult.setUserId(userId);
				drawResult.setProductId(productId);
				drawResult.setProductDetailId(selectedPrizeDetail.getProductDetailId().longValue());
				drawResult.setDrawTime(LocalDateTime.now());
				drawResult.setAmount(amount);
				drawResult.setDrawCount(1);
				drawResult.setRemainingDrawCount(remainingDrawCount);
				drawResult.setPrizeNumber(selectedPrizeNumber.getNumber());
				drawResult.setStatus("ACTIVE");
				drawResult.setTotalDrawCount((long) prizeNumbers.size());
				drawResult.setCreateDate(LocalDateTime.now());
				drawResult.setUpdateDate(LocalDateTime.now());
				drawResults.add(drawResult);

				remainingDrawCount--;
			}

			// 批量插入抽奖结果
			drawRepository.insertBatch(drawResults);

			// 生成订单
			String orderNumber = orderService.genOrderNumber();
			Order orderEntity = Order.builder()
					.userId(userId)
					.orderNumber(orderNumber)
					.createdAt(LocalDateTime.now())
					.resultStatus(OrderStatus.PREPARING_SHIPMENT)
					.totalAmount(totalAmount) // 使用总金额
					.build();
			orderRepository.insertOrder(orderEntity);

			// 获取订单ID
			Long orderId = orderRepository.getOrderIdByOrderNumber(orderNumber);
			List<OrderDetailDto> orderDetailList = new ArrayList<>();

			// 生成订单明细
			for (PrizeNumber selectedPrizeNumber : selectedPrizeNumbers) {
				// 重新获取每个奖品的 ProductDetailRes
				ProductDetailRes selectedPrizeDetail = productDetailRepository
						.getProductDetailById(Math.toIntExact(selectedPrizeNumber.getProductDetailId()));

				OrderDetailDto orderDetail = new OrderDetailDto();
				orderDetail.setOrderId(Math.toIntExact(orderId));
				orderDetail.setProductId(productId.intValue());
				orderDetail.setProductDetailId(Math.toIntExact(selectedPrizeNumber.getProductDetailId()));
				orderDetail.setProductDetailName(selectedPrizeDetail.getProductName()); // 使用重新获取的 selectedPrizeDetail
				orderDetail.setQuantity(1);
				orderDetail.setUnitPrice(amount);
				orderDetail.setResultStatus(OrderStatus.PREPARING_SHIPMENT);
				orderDetailList.add(orderDetail);
			}

			// 批量插入订单明细
			orderDetailRepository.insertOrderDetail(orderDetailList);

			// 处理用户抽奖次数和红利
			UserRes user = userRepository.getUserById(userId);
			Long drawCount = user.getDrawCount();
			if (drawCount < 3L) {
				userRepository.addDrawCount(userId);
			} else {
				userRepository.updateBonus(userId);
			}

			return drawResults;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("抽奖过程中出现错误: " + e.getMessage());
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
		ProductRes product2 = productRepository.getProductById(Math.toIntExact(productId));
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
		orderDetail.setOrderId(Math.toIntExact(orderId));
		orderDetail.setProductId(productId.intValue());
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

package com.one.frontend.service;

import com.one.frontend.eenum.OrderStatus;
import com.one.frontend.model.PrizeCartItem;
import com.one.frontend.repository.*;
import com.one.frontend.response.OrderDetailRes;
import com.one.frontend.response.OrderRes;
import com.one.frontend.response.ProductDetailRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * 測試 PaymentService.rePrizeCart() 在付款失敗時的賞品盒還原邏輯。
 *
 * 修復點：移除原本只有 paymentMethod="2" 才還原的限制，
 *         現在所有付款方式（信用卡、AFTEE 等）失敗都應還原賞品盒。
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceRePrizeCartTest {

    /* ── constructor-injected dependencies ── */
    @Mock
    private OrderTempMapper orderTempMapper;

    @Mock
    private OrderDetailTempMapper orderDetailTempMapper;

    @Mock
    private OrderRepository orderRepository;       // injected as orderMapper

    @Mock
    private OrderDetailRepository orderDetailRepository; // injected as orderDetailMapper

    /* ── field-injected dependencies ── */
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private PrizeCartRepository prizeCartRepository;

    @Mock
    private PrizeCartItemRepository prizeCartItemRepository;

    @Mock
    private PrizeCartItemService prizeCartItemService;

    @Mock
    private StoreProductRepository storeProductRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void injectFieldDependencies() {
        // @InjectMocks 透過建構子注入 orderRepository / orderDetailRepository，
        // 其餘 @Autowired 欄位需手動注入
        ReflectionTestUtils.setField(paymentService, "cartRepository", cartRepository);
        ReflectionTestUtils.setField(paymentService, "cartItemRepository", cartItemRepository);
        ReflectionTestUtils.setField(paymentService, "cartItemService", cartItemService);
        ReflectionTestUtils.setField(paymentService, "prizeCartRepository", prizeCartRepository);
        ReflectionTestUtils.setField(paymentService, "prizeCartItemRepository", prizeCartItemRepository);
        ReflectionTestUtils.setField(paymentService, "prizeCartItemService", prizeCartItemService);
        ReflectionTestUtils.setField(paymentService, "storeProductRepository", storeProductRepository);
    }

    // ─────────────────────────────────────────────────────
    //  共用 helper
    // ─────────────────────────────────────────────────────

    private OrderRes buildPrizeOrder(String paymentMethod) {
        OrderRes order = new OrderRes();
        order.setId(1L);
        order.setUserId(10L);
        order.setResultStatus(OrderStatus.NO_PAY.name());
        order.setType("2");                 // 賞品盒訂單
        order.setPaymentMethod(paymentMethod);
        return order;
    }

    private OrderDetailRes buildOrderDetail() {
        ProductDetailRes productDetail = new ProductDetailRes();
        productDetail.setProductDetailId(100L);
        productDetail.setSliverPrice(BigDecimal.valueOf(50));
        productDetail.setSize(BigDecimal.valueOf(1));

        OrderDetailRes detail = new OrderDetailRes();
        detail.setQuantity(1);
        detail.setProductDetailRes(productDetail);
        return detail;
    }

    private void stubOrderLookup(OrderRes order, List<OrderDetailRes> details) {
        when(orderRepository.findOrderByOrderNumber("ORDER123")).thenReturn(order);
        doNothing().when(orderRepository).updateStatusByFail(order.getId());
        when(orderDetailRepository.findOrderDetailsByOrderId(order.getId())).thenReturn(details);
        when(cartRepository.getCartIdByUserId(order.getUserId())).thenReturn(30L);
        when(prizeCartRepository.getCartIdByUserId(order.getUserId())).thenReturn(20L);
    }

    // ─────────────────────────────────────────────────────
    //  測試案例
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("type=2 + paymentMethod=1（信用卡）失敗 → 應還原賞品盒")
    void rePrizeCart_type2_creditCard_shouldRestorePrizeCart() throws Exception {
        OrderRes order = buildPrizeOrder("1");
        List<OrderDetailRes> details = List.of(buildOrderDetail());
        stubOrderLookup(order, details);

        paymentService.rePrizeCart("ORDER123");

        ArgumentCaptor<List<PrizeCartItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(prizeCartItemRepository).insertBatch(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getCartId()).isEqualTo(20L);
        assertThat(captor.getValue().get(0).getProductDetailId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("type=2 + paymentMethod=4（AFTEE）失敗 → 應還原賞品盒")
    void rePrizeCart_type2_aftee_shouldRestorePrizeCart() throws Exception {
        OrderRes order = buildPrizeOrder("4");
        List<OrderDetailRes> details = List.of(buildOrderDetail());
        stubOrderLookup(order, details);

        paymentService.rePrizeCart("ORDER123");

        ArgumentCaptor<List<PrizeCartItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(prizeCartItemRepository).insertBatch(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    @DisplayName("type=2 + paymentMethod=2（ATM）失敗 → 應還原賞品盒（原本就有支援，確保沒有迴歸）")
    void rePrizeCart_type2_atm_shouldRestorePrizeCart() throws Exception {
        OrderRes order = buildPrizeOrder("2");
        List<OrderDetailRes> details = List.of(buildOrderDetail());
        stubOrderLookup(order, details);

        paymentService.rePrizeCart("ORDER123");

        verify(prizeCartItemRepository).insertBatch(anyList());
    }

    @Test
    @DisplayName("訂單已是 FAILED_PAYMENT → 應拋出例外，不重複取消")
    void rePrizeCart_alreadyCancelled_shouldThrowException() {
        OrderRes order = new OrderRes();
        order.setResultStatus(OrderStatus.FAILED_PAYMENT.name());
        when(orderRepository.findOrderByOrderNumber("ORDER123")).thenReturn(order);

        assertThatThrownBy(() -> paymentService.rePrizeCart("ORDER123"))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("已經取消過了");

        verifyNoInteractions(prizeCartItemRepository);
    }

    @Test
    @DisplayName("type=2 但訂單明細為空 → 不呼叫 insertBatch（防止空 batch insert）")
    void rePrizeCart_type2_emptyDetails_shouldNotCallInsertBatch() throws Exception {
        OrderRes order = buildPrizeOrder("1");
        stubOrderLookup(order, List.of());  // 空明細

        paymentService.rePrizeCart("ORDER123");

        verifyNoInteractions(prizeCartItemRepository);
    }
}

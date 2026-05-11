package com.one.frontend.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.PrizeCartItem;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.request.PayCartRes;
import com.one.frontend.response.OrderPayRes;
import com.one.frontend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 測試 OrderController.payPrizeCartItem() 的空購物車守衛邏輯。
 *
 * 修復點：若後端查回的 prizeCartItemList 為空（舊 ID 已不存在），
 *         應直接回傳 400 而非建立一筆無明細的訂單。
 *
 * 注意：prizeCartService / prizeCartItemService 是 @Autowired field injection，
 *       @InjectMocks 只透過 constructor 注入 final 欄位，
 *       所以用 ReflectionTestUtils.setField() 手動補注。
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerPayPrizeCartTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private CartService cartService;

    @Mock
    private PrizeCartService prizeCartService;

    @Mock
    private PrizeCartItemService prizeCartItemService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void injectFieldDependencies() {
        // prizeCartService / prizeCartItemService 用 @Autowired field injection，
        // 需要手動補注才能被 @InjectMocks 產生的 controller 取用。
        ReflectionTestUtils.setField(orderController, "prizeCartService", prizeCartService);
        ReflectionTestUtils.setField(orderController, "prizeCartItemService", prizeCartItemService);
    }

    // ─────────────────────────────────────────────────────
    //  helper
    // ─────────────────────────────────────────────────────

    private CustomUserDetails mockUser(Long userId) {
        return CustomUserDetails.builder().id(userId).build();
    }

    // ─────────────────────────────────────────────────────
    //  測試案例
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("prizeCartItemList 為空（舊 ID 已刪除）→ 回傳 400，不建立訂單")
    void payPrizeCartItem_emptyCart_shouldReturn400() {
        try (MockedStatic<SecurityUtils> staticSecurity = mockStatic(SecurityUtils.class)) {
            // 模擬登入使用者
            staticSecurity.when(SecurityUtils::getCurrentUserPrinciple)
                    .thenReturn(mockUser(1L));

            // 模擬購物車存在，但查回的 item 清單是空的（ID 已被前一筆訂單刪除）
            when(prizeCartService.getCartIdByUserId(1L)).thenReturn(10L);
            PayCartRes payCartRes = new PayCartRes();
            payCartRes.setPrizeCartItemIds(List.of(999L));
            when(prizeCartItemService.findByCartIdAndCartItemList(10L, List.of(999L)))
                    .thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<?> response = orderController.payPrizeCartItem(payCartRes);

            // Assert：應回傳 400，且完全不呼叫 createPrizeOrder
            ApiResponse<?> body = (ApiResponse<?>) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo(400);
            assertThat(body.getMessage()).contains("購物車為空");
            assertThat(body.isSuccess()).isFalse();
            verifyNoInteractions(orderService);
        }
    }

    @Test
    @DisplayName("prizeCartItemList 有資料 → 呼叫 createPrizeOrder 並回傳 200")
    void payPrizeCartItem_nonEmptyCart_shouldCallCreatePrizeOrderAndReturn200() throws Exception {
        try (MockedStatic<SecurityUtils> staticSecurity = mockStatic(SecurityUtils.class)) {
            staticSecurity.when(SecurityUtils::getCurrentUserPrinciple)
                    .thenReturn(mockUser(1L));

            PrizeCartItem item = new PrizeCartItem();
            when(prizeCartService.getCartIdByUserId(1L)).thenReturn(10L);
            PayCartRes payCartRes = new PayCartRes();
            payCartRes.setPrizeCartItemIds(List.of(1L));
            when(prizeCartItemService.findByCartIdAndCartItemList(eq(10L), eq(List.of(1L))))
                    .thenReturn(List.of(item));

            OrderPayRes orderPayRes = new OrderPayRes("ORDER123", "2", null);
            when(orderService.createPrizeOrder(any(), eq(List.of(item)), eq(1L)))
                    .thenReturn(orderPayRes);

            // Act
            ResponseEntity<?> response = orderController.payPrizeCartItem(payCartRes);

            // Assert：createPrizeOrder 應被呼叫，回傳 200
            verify(orderService).createPrizeOrder(any(), eq(List.of(item)), eq(1L));
            ApiResponse<?> body = (ApiResponse<?>) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo(200);
            assertThat(body.isSuccess()).isTrue();
        }
    }

    @Test
    @DisplayName("找不到 cartId → 回傳 999，不進入購物車查詢")
    void payPrizeCartItem_nullCartId_shouldReturn999() {
        try (MockedStatic<SecurityUtils> staticSecurity = mockStatic(SecurityUtils.class)) {
            staticSecurity.when(SecurityUtils::getCurrentUserPrinciple)
                    .thenReturn(mockUser(1L));
            when(prizeCartService.getCartIdByUserId(1L)).thenReturn(null);

            ResponseEntity<?> response = orderController.payPrizeCartItem(new PayCartRes());

            ApiResponse<?> body = (ApiResponse<?>) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo(999);
            verifyNoInteractions(prizeCartItemService);
            verifyNoInteractions(orderService);
        }
    }
}

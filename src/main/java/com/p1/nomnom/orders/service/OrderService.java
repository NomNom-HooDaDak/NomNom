package com.p1.nomnom.orders.service;

import com.p1.nomnom.orders.dto.request.OrderSearchRequestDto;
import com.p1.nomnom.orders.dto.response.OrderListResponseDto;
import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.orders.util.AuthorizationUtil;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.repository.StoreRepository;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orderItems.service.OrderItemService;
import com.p1.nomnom.orders.dto.request.OrderRequestDto;
import com.p1.nomnom.orders.dto.response.OrderResponseDto;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final StoreRepository storeRepository;

    // 주문 등록
    @Transactional
    public OrderResponseDto createOrder(UserContext userContext, OrderRequestDto orderRequestDto) {
        String storeName = storeRepository.findStoreNameById(orderRequestDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        Order order = Order.create(
                orderRequestDto.getStoreId(),
                storeName,
                userContext.getUser(),
                orderRequestDto.getPhone(),
                orderRequestDto.getAddressId(),
                orderRequestDto.getRequest()
        );
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = orderItemService.createOrderItems(order, orderRequestDto.getOrderItems());
        Long totalPrice = orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        savedOrder.updateOrderItemsAndTotalPrice(orderItems, totalPrice);

        orderRepository.save(savedOrder);
        return OrderResponseDto.from(savedOrder);
    }

    // ✅ 특정 주문 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(UUID orderId, UserContext userContext) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        AuthorizationUtil.validateOrderPermission(userContext, order, order.getStoreId(), storeRepository);
        return OrderResponseDto.from(order);
    }

    // ✅ 주문 리스트 조회
    @Transactional(readOnly = true)
    public OrderListResponseDto getOrders(UserContext userContext, OrderSearchRequestDto searchRequest) {
        Page<Order> ordersPage = orderRepository.searchOrders(userContext, searchRequest);
        return OrderListResponseDto.from(ordersPage);
    }

    // ✅ 주문 취소
    @Transactional
    public void cancelOrder(UUID orderId, UserContext userContext) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        AuthorizationUtil.validateOrderPermission(userContext, order, order.getStoreId(), storeRepository);
        order.cancel();
        orderRepository.save(order);
    }

    // ✅ 주문 삭제 (숨김 처리)
    @Transactional
    public void deleteOrder(UUID orderId, UserContext userContext) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        AuthorizationUtil.validateOrderPermission(userContext, order, order.getStoreId(), storeRepository);
        order.delete(userContext.getUsername());
        orderRepository.save(order);
    }

    // 주문 배달 상태 확인 (리뷰서비스에서 사용)
    public boolean isOrderDelivered(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다."));
        return order.getStatus() == Status.DELIVERED;
    }
}

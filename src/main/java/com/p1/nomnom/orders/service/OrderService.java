package com.p1.nomnom.orders.service;

import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.common.aop.UserContext;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orderItems.service.OrderItemService;
import com.p1.nomnom.orders.dto.request.OrderRequestDto;
import com.p1.nomnom.orders.dto.response.OrderResponseDto;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.repository.OrderRepository;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;

    // 주문 등록
    @Transactional
    public OrderResponseDto createOrder(UserContext userContext, OrderRequestDto orderRequestDto) {
        Order order = Order.create(
                orderRequestDto.getStoreId(),
                userContext.getUser(),
                orderRequestDto.getPhone(),
                orderRequestDto.getAddressId(),
                orderRequestDto.getRequest()
        );
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = orderItemService.createOrderItems(savedOrder, orderRequestDto.getOrderItems());

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

        if (userContext.getRole() == UserRoleEnum.CUSTOMER && !order.getUser().getId().equals(userContext.getUserId())) {
            throw new IllegalArgumentException("본인이 주문한 것만 조회할 수 있습니다.");
        }

        if (userContext.getRole() == UserRoleEnum.OWNER && !order.getStoreId().equals(userContext.getUserId())) {
            throw new IllegalArgumentException("가게 주인은 해당 가게의 주문만 조회할 수 있습니다.");
        }

        return OrderResponseDto.from(order);
    }

    // ✅ 주문 리스트 조회
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(UserContext userContext, Pageable pageable) {
        if (userContext.getRole() == UserRoleEnum.MASTER || userContext.getRole() == UserRoleEnum.MANAGER) {
            return orderRepository.findAll(pageable).map(OrderResponseDto::from);
        }

        if (userContext.getRole() == UserRoleEnum.OWNER) {
            return orderRepository.findByStoreIdOrderByCreatedAtDesc(userContext.getUserId(), pageable)
                    .map(OrderResponseDto::from);
        }

        // CUSTOMER는 자신이 주문한 것만 조회
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userContext.getUserId(), pageable)
                .map(OrderResponseDto::from);
    }

    // ✅ 주문 취소
    @Transactional
    public void cancelOrder(UUID orderId, UserContext userContext) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        if (userContext.getRole() == UserRoleEnum.CUSTOMER) {
            if (!order.getUser().getId().equals(userContext.getUserId())) {
                throw new IllegalArgumentException("본인이 주문한 것만 취소할 수 있습니다.");
            }
        }

        if (userContext.getRole() == UserRoleEnum.OWNER) {
            if (!order.getStoreId().equals(userContext.getUserId())) {
                throw new IllegalArgumentException("가게 주인은 본인 가게의 주문만 취소할 수 있습니다.");
            }
        }

        order.cancel(userContext.getUsername());

        orderRepository.save(order);
    }

    // 주문 배달 상태 확인 (리뷰서비스에서 사용)
    public boolean isOrderDelivered(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다."));
        return order.getStatus() == Status.DELIVERED;
    }
}

package com.p1.nomnom.orders.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemService orderItemService;

    // 주문 등록
    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Order order = Order.create(
                orderRequestDto.getStoreId(),
                user,
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
}

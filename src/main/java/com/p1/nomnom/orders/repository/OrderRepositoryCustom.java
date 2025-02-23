package com.p1.nomnom.orders.repository;

import com.p1.nomnom.orders.dto.request.OrderSearchRequestDto;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.user.entity.UserRoleEnum;
import org.springframework.data.domain.Page;


public interface OrderRepositoryCustom {
    Page<Order> searchOrders(UserContext userContext, OrderSearchRequestDto searchRequest);
}

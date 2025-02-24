package com.p1.nomnom.orders.util;

import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.entity.QOrder;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.repository.StoreRepository;
import com.querydsl.core.BooleanBuilder;

import java.util.UUID;

public class AuthorizationUtil {

    public static BooleanBuilder buildOrderPermission(UserContext userContext, QOrder order, UUID storeId, StoreRepository storeRepository) {
        BooleanBuilder whereClause = new BooleanBuilder();

        switch (userContext.getRole()) {
            case CUSTOMER -> whereClause.and(order.user.id.eq(userContext.getUserId()))
                    .and(order.hidden.eq(false));

            case OWNER -> {
                if (storeId == null) {
                    throw new IllegalArgumentException("가게 ID가 필요합니다.");
                }
                boolean isOwner = storeRepository.existsByIdAndUserId(storeId, userContext.getUserId());
                if (!isOwner) {
                    throw new IllegalArgumentException("가게 주인만 해당 작업을 수행할 수 있습니다.");
                }
                whereClause.and(order.storeId.eq(storeId))
                        .and(order.hidden.eq(false));
            }

            case MANAGER, MASTER -> {
                if (storeId == null) {
                    throw new IllegalArgumentException("가게 ID가 필요합니다.");
                }
                whereClause.and(order.storeId.eq(storeId));
            }

            default -> throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }

        return whereClause;
    }

    public static void validateOrderPermission(UserContext userContext, Order order, UUID storeId, StoreRepository storeRepository) {
        switch (userContext.getRole()) {
            case CUSTOMER -> {
                if (!order.getUser().getId().equals(userContext.getUserId())) {
                    throw new IllegalArgumentException("본인이 주문한 것만 조회할 수 있습니다.");
                }
            }
            case OWNER -> {
                if (storeId == null) {
                    throw new IllegalArgumentException("가게 ID가 필요합니다.");
                }
                boolean isOwner = storeRepository.existsByIdAndUserId(storeId, userContext.getUserId());
                if (!isOwner) {
                    throw new IllegalArgumentException("가게 주인만 해당 작업을 수행할 수 있습니다.");
                }
            }
            case MANAGER, MASTER -> {
                // 모든 주문 접근 가능
            }
            default -> throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

}

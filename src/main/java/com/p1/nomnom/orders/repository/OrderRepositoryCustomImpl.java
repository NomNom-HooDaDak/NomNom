package com.p1.nomnom.orders.repository;

import com.p1.nomnom.orderItems.entity.QOrderItem;
import com.p1.nomnom.orders.dto.request.OrderSearchRequestDto;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.entity.QOrder;
import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.orders.util.AuthorizationUtil;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.repository.StoreRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final StoreRepository storeRepository;

    @Override
    public Page<Order> searchOrders(UserContext userContext, OrderSearchRequestDto searchRequest) {
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        // 권한 체크
        BooleanBuilder whereClause = AuthorizationUtil.buildOrderPermission(
                userContext, order, searchRequest.getStoreId(), storeRepository
        );

        // 키워드 검색 (storeName, foodName 동시 검색)
        if (org.springframework.util.StringUtils.hasText(searchRequest.getKeyword())) {
            whereClause.and(
                    order.storeName.containsIgnoreCase(searchRequest.getKeyword())
                            .or(JPAExpressions.selectOne()
                                    .from(orderItem)
                                    .where(orderItem.order.id.eq(order.id)
                                            .and(orderItem.foodName.containsIgnoreCase(searchRequest.getKeyword())))
                                    .exists()
                            )
            );
        }

        // 주문 상태 필터링 (예: 진행중, 완료, 취소)
        if (searchRequest.getFilterKey() != null && searchRequest.getFilterValue() != null) {
            if ("status".equals(searchRequest.getFilterKey())) {
                switch (searchRequest.getFilterValue()) {
                    case "진행중" -> whereClause.and(order.status.in(Status.PENDING, Status.CONFIRMED, Status.SHIPPED));
                    case "완료됨" -> whereClause.and(order.status.eq(Status.DELIVERED));
                    case "취소됨" -> whereClause.and(order.status.eq(Status.CANCELED));
                }
            }
        }


        // 정렬 방향 설정 (고정된 createdAt 기준)
        String sortDirection = Optional.ofNullable(searchRequest.getSort()).orElse("desc");
        OrderSpecifier<?> orderSpecifier = "asc".equalsIgnoreCase(sortDirection)
                ? order.createdAt.asc()
                : order.createdAt.desc();

        // 쿼리 실행
        var query = queryFactory
                .selectFrom(order)
                .where(whereClause)
                .orderBy(orderSpecifier)
                .offset(searchRequest.toPageable().getOffset())
                .limit(searchRequest.toPageable().getPageSize());

        List<Order> orders = query.fetch();

        return PageableExecutionUtils.getPage(orders, searchRequest.toPageable(), query::fetchCount);
    }
}


package com.p1.nomnom.payment.service;

import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.orders.repository.OrderRepository;
import com.p1.nomnom.payment.dto.response.PaymentResponseDto;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.payment.repository.PaymentRepository;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.store.repository.StoreRepository;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    // 결제 승인 요청 및 검증
    @Transactional
    public PaymentResponseDto paymentConfirm(String username, UUID orderId, Long totalPrice, String payType) {
        // 1. orderId 가 존재하는지 확인해야 함
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("결제할 주문 내역이 없습니다.")
        );

        // log.info("orderId 전달해서 order정보 얻기: {}", order.toString());
        // log.info("user {}", order.getUser());
        // log.info("order.getStatus(): {}", order.getStatus());
        log.info("username: {}", username); // 토큰에서 추출한 사용자 이름
        // order 테이블에서 user 를 얻고, user의 이름을 얻는다.
        log.info("order.getUser().getUsername: {}", order.getUser().getUsername());

        if(!(order.getUser().getUsername().equals(username))) {

            // 전달받은 토큰 정보(user)와 데이터베이스에 저장된 user의 정보를 비교한다
            // 사용자가 본인이 주문한 정보에만 접근할 수 있도록 한번 더 체크함
            throw new SecurityException("권한이 없는 결제 요청입니다.");
        }

        // 2. order 객체로부터 status 와 totalPrice 를 확인한다.
       if(order.getStatus() != Status.CONFIRMED) {
           throw new IllegalArgumentException("주문을 먼저 완료해주세요!");
       }

       // 3. 데이터베이스에 저장된 주문서의 금액과 클라이언트가 전달한 결제 금액이 일치하지 않는 경우 있음
        // 보안의 문제로 금액을 체크한다
       if(!(order.getTotalPrice().equals(totalPrice))) {
            throw new SecurityException("결제 금액과 주문 금액이 일치하지 않습니다.");
       }

        Store store = storeRepository.findById(order.getStoreId()).orElseThrow(() -> new IllegalArgumentException("결제한 가게가 존재하지 않습니다."));

        if(payType.equals("CARD")) {
           Payment cardPayment = new Payment();
           cardPayment.setStore(store);
           cardPayment.setStatus(Payment.Status.PROGRESS);
           cardPayment.setMethod(Payment.Method.CARD);
           cardPayment.setUserId(order.getUser().getId());
           cardPayment.setOrder(order);
           cardPayment.setCreatedBy(username);
           cardPayment.createPaymentKey();

            Payment requestCardPayment = Optional.of(paymentRepository.save(cardPayment))
                    .orElseThrow(()->new IllegalArgumentException("카드 결제 요청이 승인되지 않았습니다."));

            PaymentResponseDto paymentConfirmCardResponseDto = new PaymentResponseDto(requestCardPayment);
            log.info("카드 결제 승인 정보: {}", paymentConfirmCardResponseDto);
            return paymentConfirmCardResponseDto;

        } else {
            Payment checkPayment = new Payment();
            checkPayment.setStore(store);
            checkPayment.setStatus(Payment.Status.SUCCESS); // 현금 결제는 바로 결제 승인
            checkPayment.setMethod(Payment.Method.CHECK);
            checkPayment.setUserId(order.getUser().getId());
            checkPayment.setOrder(order);
            checkPayment.setCreatedBy(username);
            checkPayment.createPaymentKey();

            log.info("checkPayment {}", checkPayment);

            Payment requestCheckPayment = Optional.ofNullable(paymentRepository.save(checkPayment))
                    .orElseThrow(()->new IllegalArgumentException("현금 결제가 완료되지 않았습니다."));

            PaymentResponseDto paymentCheckResponseDto = new PaymentResponseDto(requestCheckPayment);
            log.info("현금 결제 승인 정보: {}", paymentCheckResponseDto);
            return paymentCheckResponseDto;
        }
    }


    public PaymentResponseDto getPaymentInfoOne(UserContext userContext, UUID paymentUUID) {
        Payment findPayment = paymentRepository.findById(paymentUUID).orElseThrow(()-> new IllegalArgumentException("조회하신 결제 내역이 존재하지 않습니다."));

        log.info("userContext: {}",userContext.getUser()); // userContext: com.p1.nomnom.user.entity.User@2bf1d657
        log.info("userContext.getUsername(): {}", userContext.getUsername()); // testuser1
        log.info("findPayment: {}", findPayment.toString());

        // 결제 내역을 조회한 user 정보
        User user = userContext.getUser();

        // 결제 내역의 가게 정보 가져오기: Payment 를 통해 Store 객체를 얻는다.
        Store store = findPayment.getStore();
        // Store에 저장되어 있는 user의 id 를 얻는다.
        User storeUser = store.getUser();

        if (user.getRole() == UserRoleEnum.CUSTOMER && !user.getId().equals(findPayment.getUserId())) {
            throw new IllegalArgumentException("결제자와 결제내역 조회자 불일치로 해당 결제 내역을 조회할 권한이 없습니다.");
        }
        if (user.getRole() == UserRoleEnum.OWNER && !user.getId().equals(storeUser.getId())) {
            throw new IllegalArgumentException("자신의 가게에서 발생한 결제 내역만 조회할 수 있습니다.");
        }
        return new PaymentResponseDto(findPayment);
    }

    @Transactional
    public PaymentResponseDto cancel(UserContext userContext, UUID paymentUUID) {
        Payment findPayment = paymentRepository.findById(paymentUUID)
                .orElseThrow(() -> new IllegalArgumentException("조회하신 결제 내역이 존재하지 않습니다."));

        if (!findPayment.getUserId().equals(userContext.getUserId())) {
            throw new SecurityException("결제 내역을 조회할 권한이 없습니다.");
        }
        findPayment.setStatus(Payment.Status.FAIL);
        findPayment.setDeletedBy(userContext.getUsername());

        Payment cancelPayment = paymentRepository.save(findPayment);
        return new PaymentResponseDto(cancelPayment);
    }
}

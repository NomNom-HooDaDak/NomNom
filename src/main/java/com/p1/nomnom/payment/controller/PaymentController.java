package com.p1.nomnom.payment.controller;

import com.p1.nomnom.payment.dto.response.PaymentResponseDto;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.payment.service.PaymentService;
import com.p1.nomnom.security.aop.CurrentUser;
import com.p1.nomnom.security.aop.CurrentUserInject;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payment")
@Slf4j(topic="payment controller")
public class PaymentController {
    private final JwtUtil jwtUtil;
    private final PaymentService paymentService;

    // 결제 요청이 들어왔고,
    // 해당 결제 요청의 검증 여부를 통해
    // 테이블에 저장할 것임
    @RoleCheck({UserRoleEnum.CUSTOMER})
    @GetMapping("/confirm") // http://localhost:8080/api/payment/confirm
    @Operation(summary = "결제 (승인) 요청", description = "카드 - 결제 요청을 승인합니다, 현금 - 결제를 완료합니다.")
    @ApiResponse(responseCode = "200", description = "주문 등록 성공")
    public ResponseEntity<?> paymentConfirm(@RequestHeader("Authorization") String token,
                                            @RequestParam String payType,
                                            @RequestParam UUID orderId,
                                            @RequestParam Long totalPrice) {
        log.info("api/payment/confirm - GET, orderId: {}, {}", orderId, totalPrice);
        log.info("token: {}", token);

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String username = claims.getSubject();

        // log.info("username: {}", username);

        PaymentResponseDto paymentResponseDto = paymentService.paymentConfirm(username, orderId, totalPrice, payType);
        log.info("paymentResponseDto: {}", paymentResponseDto.toString());

        if((paymentResponseDto.getPaymentMethod() == Payment.Method.CARD) && paymentResponseDto.getCurrentStatus() == Payment.CurrentStatus.PROGRESS)
        {
            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "카드 결제 요청이 승인되었습니다. 결제를 진행하세요.");
            response.put("confirmPaymentInfo", paymentResponseDto);

            return ResponseEntity.ok().body(response);
        }

        if((paymentResponseDto.getPaymentMethod() == Payment.Method.CHECK) && paymentResponseDto.getCurrentStatus() == Payment.CurrentStatus.SUCCESS)
        {
            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "현금 결제가 완료되었습니다.");
            response.put("confirmPaymentInfo", paymentResponseDto);

            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body("잘못된 결제 요청입니다.");
    }

    @Operation(summary = "단일 결제 내역 조회", description = "특정 결제 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "단일 결제 내역 조회 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.MASTER, UserRoleEnum.MANAGER, UserRoleEnum.OWNER})
    @CurrentUserInject
    @GetMapping("/pay_list")
    public ResponseEntity<?> getPaymentInfoOne(
            @Parameter(description = "결제 ID(UUID)", required = true)
            @RequestParam UUID paymentUUID,
            @CurrentUser @Parameter(hidden = true) UserContext userContext) // user 를 받아옴
    {
        log.info("api/payment/pay_list - GET");
        PaymentResponseDto paymentInfoOne = paymentService.getPaymentInfoOne(userContext, paymentUUID);

        return ResponseEntity.ok().body(paymentInfoOne);
    }


}

package com.p1.nomnom.payment.controller;

import com.p1.nomnom.payment.dto.response.PaymentResponseDto;
import com.p1.nomnom.payment.service.PaymentService;
import com.p1.nomnom.security.aop.CurrentUser;
import com.p1.nomnom.security.aop.CurrentUserInject;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
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
    @Operation(summary = "결제 요청", description = "결제를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "결제 요청 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER})
    @CurrentUserInject
    @GetMapping("/request") // http://localhost:8080/api/payment/confirm
    public ResponseEntity<?> paymentRequest(@CurrentUser @Parameter(hidden = true) UserContext userContext,
                                            @RequestParam String payType,
                                            @RequestParam UUID orderId,
                                            @RequestParam Long totalPrice) {
        log.info("api/payment/confirm - GET, orderId: {}, {}", orderId, totalPrice);

        try {
            PaymentResponseDto paymentResponseDto = paymentService.paymentRequest(userContext, orderId, totalPrice, payType);
            log.info("paymentResponseDto: {}", paymentResponseDto.toString());

            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "결제가 완료되었습니다.");
            response.put("confirmPaymentInfo", paymentResponseDto);
            return ResponseEntity.ok().body(response);

        } catch (EntityNotFoundException e) {
            log.warn("잘못된 요청 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("권한 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }  catch(IllegalArgumentException e) {
            log.warn("결제 금액 검증 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }   catch (IllegalStateException e) {
            log.warn("상태 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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

    // 결제 취소는 결재했던 유저인 CUSTOMER만 가능
    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    @ApiResponse(responseCode = "200", description = "결제 취소 성공")
    @CurrentUserInject
    @RoleCheck({UserRoleEnum.CUSTOMER})
    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelPayment(
            @Parameter(description = "결제 ID(UUID)", required = true)
            @RequestParam UUID paymentUUID,
            @CurrentUser @Parameter(hidden = true) UserContext userContext) throws AccessDeniedException // user 를 받아옴
    {
        log.info("api/payment/cancel - PUT");
        PaymentResponseDto cancelPaymentInfo = paymentService.cancel(userContext, paymentUUID);
        HashMap<String, Object> response = new HashMap<>();
        response.put("cancelPaymentInfo", cancelPaymentInfo);
        response.put("message", "결제가 취소되었습니다.");
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "결제 내역 숨김", description = "결제 내역을 숨김처리 합니다.")
    @ApiResponse(responseCode = "200", description = "결제 내역 숨김 성공")
    @CurrentUserInject
    @RoleCheck({UserRoleEnum.CUSTOMER})
    @PatchMapping("/hide")
    public ResponseEntity<?> hidePayment(@CurrentUser @Parameter(hidden = true) UserContext userContext,
                                         @Parameter(description = "결제 ID(UUID)", required = true)
                                         @RequestParam UUID paymentUUID) throws AccessDeniedException {
        log.info("api/payment/hide - GET");

        PaymentResponseDto paymentResponseDto = paymentService.hidePayment(userContext, paymentUUID);
        return ResponseEntity.ok().body(paymentResponseDto);
    }
}

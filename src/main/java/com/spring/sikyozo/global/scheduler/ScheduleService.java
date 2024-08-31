package com.spring.sikyozo.global.scheduler;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.spring.sikyozo.domain.payment.entity.QPayment.payment;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final JPAQueryFactory jpaQueryFactory;

    /*
     * 기준 시간 초과한 미결제 내역 취소
     */

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void deletePaymentIfNotPaid() {

        log.info("미결제 내역을 조회합니다.");

        LocalDateTime conditionTime = LocalDateTime.now().minusMinutes(5);

        List<Payment> payments = jpaQueryFactory
                .selectFrom(payment)
                .where(
                        payment.status.eq(PaymentStatus.PENDING),
                        payment.createdAt.before(conditionTime)
                )
                .fetch();
        for (Payment payment : payments) {
            log.info("결제 시간이 초과하여 결제를 취소합니다. paymentId: {}", payment.getId());
            payment.cancel(null);
        }
    }
}

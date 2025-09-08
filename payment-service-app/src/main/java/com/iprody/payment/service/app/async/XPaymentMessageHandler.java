package com.iprody.payment.service.app.async;

import com.iprody.async.MessageHandler;
import com.iprody.async.XPaymentAdapterResponseMessage;
import com.iprody.async.XPaymentAdapterStatus;
import com.iprody.payment.service.app.exception.NotFoundException;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("[XPaymentMessageHandler] ResponseMessage is handling {}:", message);

        final UUID paymentGuid = message.getPaymentGuid();
        final Payment payment = paymentRepository.findById(paymentGuid)
            .orElseThrow(() -> new NotFoundException("Payment not found", "handle", paymentGuid));

        payment.setAmount(message.getAmount());
        payment.setCurrency(message.getCurrency());
        payment.setStatus(mapStatus(message.getStatus()));
        payment.setUpdatedAt(message.getOccurredAt());

        log.info("[XPaymentMessageHandler] Payment is successfully updated {}:", payment);
        paymentRepository.save(payment);
    }

    private PaymentStatus mapStatus(XPaymentAdapterStatus adapterStatus) {
        return switch (adapterStatus) {
            case PROCESSING -> PaymentStatus.PENDING;
            case SUCCEEDED -> PaymentStatus.APPROVED;
            case CANCELED -> PaymentStatus.DECLINED;
        };
    }
}

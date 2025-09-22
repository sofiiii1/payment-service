package com.iprody.xpaymentadapterapp.checkstate.handler;

import com.iprody.async.XPaymentAdapterResponseMessage;
import com.iprody.async.XPaymentAdapterStatus;
import com.iprody.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {

    private final XPaymentProviderGateway gateway;
    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> kafkaTemplate;
    @Value("${app.kafka.topics.xpayment-adapter.response}") String topic;


    @Override
    public boolean handle(UUID chargeGuid) {
        final ChargeResponseDto responseDto = this.gateway.retrieveCharge(chargeGuid);
        final XPaymentAdapterStatus status = responseDto.getStatus();
        log.info("Payment {} status {} handled, sending Kafka notification", chargeGuid, status);
        if (status == XPaymentAdapterStatus.SUCCEEDED || status == XPaymentAdapterStatus.CANCELED) {
            final XPaymentAdapterResponseMessage response = new XPaymentAdapterResponseMessage();
            response.setPaymentGuid(chargeGuid);
            response.setTransactionRefId(responseDto.getOrder());
            response.setAmount(responseDto.getAmount());
            response.setCurrency(responseDto.getCurrency());
            response.setStatus(status);
            response.setOccurredAt(OffsetDateTime.parse(responseDto.getCreatedAt()));
            kafkaTemplate.send(topic, String.valueOf(chargeGuid), response);
            return true;
        }
        return false;
    }
}

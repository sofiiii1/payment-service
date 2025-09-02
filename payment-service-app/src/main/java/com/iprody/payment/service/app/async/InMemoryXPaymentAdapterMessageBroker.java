package com.iprody.payment.service.app.async;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        log.info("[XPaymentAdapterMessageBroker] RequestMessage was received: {}", request);
        final UUID txId = UUID.randomUUID();
        scheduler.schedule(() -> emit(request, txId), 30, TimeUnit.SECONDS);
        log.info("[XPaymentAdapterMessageBroker] RequestMessage was sent to XPaymentAdapterResultListenerAdapter");
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId) {
        final XPaymentAdapterStatus status =
            request.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0
            ? XPaymentAdapterStatus.SUCCEEDED : XPaymentAdapterStatus.CANCELED;

        final XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        result.setMessageGuid(request.getMessageId());
        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(status);
        result.setOccurredAt(OffsetDateTime.now());

        log.info("[XPaymentAdapterMessageBroker] ResponseMessage handling is done: {}", result);

        resultListener.onMessage(result);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}

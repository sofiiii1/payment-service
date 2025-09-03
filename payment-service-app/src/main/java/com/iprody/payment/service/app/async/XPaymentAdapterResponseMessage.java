package com.iprody.payment.service.app.async;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сообщение-ответ от платёжной системы X Payment.
 * <p>
 * Содержит сведения о результате обработки платежа, включая его
 идентификаторы,
 * сумму, валюту, ссылку на транзакцию, статус и момент времени
 события.
 * Реализует интерфейс {@link Message}, предоставляя уникальный
 идентификатор сообщения
 * и метку времени его возникновения.
 */
@Getter
@Setter
@ToString
public class XPaymentAdapterResponseMessage implements Message {
    /**
     * Уникальный идентификатор сообщения.
     */

    private UUID messageGuid;
    /**
     * Уникальный идентификатор платежа.
     */
    private UUID paymentGuid;
    /**
     * Сумма платежа.
     */
    private BigDecimal amount;
    /**
     * Валюта платежа в формате ISO 4217 (например, "USD", "EUR").
     */
    private String currency;
    /**
     * Уникальный идентификатор транзакции в платёжной системе.
     */
    private UUID transactionRefId;
    /**
     * Статус платежа.
     */
    private XPaymentAdapterStatus status;
    /**
     * Момент времени, когда событие произошло.
     */
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return messageGuid;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}

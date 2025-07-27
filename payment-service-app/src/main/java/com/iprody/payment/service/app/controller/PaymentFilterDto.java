package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentFilterDto {
    private String currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private PaymentStatus status;
    private Instant createdAtBefore;
    private Instant createdAtAfter;
}

package com.iprody.xpaymentadapterapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateChargeRequestDto {

    private BigDecimal amount;

    private String currency;

    private String customer;

    private UUID order;

    private String receiptEmail;

    private Map<String, Object> metadata;
}

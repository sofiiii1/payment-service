package com.iprody.xpaymentadapterapp.checkstate;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentCheckStateMessage {

    private UUID chargeGuid;
    private UUID paymentGuid;
    private BigDecimal amount;
    private String currency;
}

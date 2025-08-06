package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class PaymentStatusUpdateDto {

    @NotNull
    private PaymentStatus status;
}

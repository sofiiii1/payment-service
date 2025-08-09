package com.iprody.payment.service.app.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class PaymentNoteUpdateDto {

    @NotNull
    private String note;
}

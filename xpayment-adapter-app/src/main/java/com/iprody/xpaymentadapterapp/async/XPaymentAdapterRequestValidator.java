package com.iprody.xpaymentadapterapp.async;

import com.iprody.async.XPaymentAdapterRequestMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;

@Component
public class XPaymentAdapterRequestValidator {

    public boolean isValid(XPaymentAdapterRequestMessage message) {
        if (message.getAmount() == null || message.getCurrency() == null) {
            return false;
        }
        if (message.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        try {
            final Currency currency = Currency.getInstance(message.getCurrency());
            final int fractionDigits = currency.getDefaultFractionDigits();
            final int actualScale = message.getAmount().scale();
            if (actualScale > fractionDigits) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}

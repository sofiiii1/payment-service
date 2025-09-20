package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.api.model.CreateChargeRequest;

import java.util.UUID;

public interface XPaymentService {
    ChargeResponse createCharge(CreateChargeRequest request);
    ChargeResponse retrieveCharge(UUID uuid);
}

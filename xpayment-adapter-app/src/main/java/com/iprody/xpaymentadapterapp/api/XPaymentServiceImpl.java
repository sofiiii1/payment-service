package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.client.DefaultApi;
import com.iprody.xpaymentadapterapp.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.api.model.CreateChargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XPaymentServiceImpl implements XPaymentService {

    private final DefaultApi defaultApi;

    @Override
    public ChargeResponse createCharge(CreateChargeRequest request) {
        return defaultApi.createCharge(request);
    }

    @Override
    public ChargeResponse retrieveCharge(UUID uuid) {
        return defaultApi.retrieveCharge(uuid);
    }
}

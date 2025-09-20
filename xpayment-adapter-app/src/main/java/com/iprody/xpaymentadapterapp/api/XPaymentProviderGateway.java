package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;

import java.util.UUID;

public interface XPaymentProviderGateway {
    ChargeResponseDto createCharge(CreateChargeRequestDto request);
    ChargeResponseDto retrieveCharge(UUID uuid);
}

package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.client.DefaultApi;
import com.iprody.xpaymentadapterapp.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.api.model.CreateChargeRequest;
import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.mapper.ChargeResponseMapper;
import com.iprody.xpaymentadapterapp.mapper.CreateChargeRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;
    private final CreateChargeRequestMapper createChargeRequestMapper;
    private final ChargeResponseMapper chargeResponseMapper;

    @Override
    public ChargeResponseDto createCharge(CreateChargeRequestDto request) throws RestClientException {
        final CreateChargeRequest createChargeRequest = createChargeRequestMapper.toCreateChargeRequest(request);
        final ChargeResponse chargeResponse = defaultApi.createCharge(createChargeRequest);
        return chargeResponseMapper.toChargeResponseDto(chargeResponse);
    }

    @Override
    public ChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        return chargeResponseMapper.toChargeResponseDto(defaultApi.retrieveCharge(id));
    }
}

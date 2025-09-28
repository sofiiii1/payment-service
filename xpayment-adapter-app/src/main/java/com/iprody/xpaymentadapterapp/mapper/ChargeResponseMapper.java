package com.iprody.xpaymentadapterapp.mapper;

import com.iprody.xpaymentadapterapp.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeResponseMapper {
    ChargeResponse toChargeResponse(ChargeResponseDto dto);
    ChargeResponseDto toChargeResponseDto(ChargeResponse chargeResponse);
}

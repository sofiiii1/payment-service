package com.iprody.xpaymentadapterapp.mapper;

import com.iprody.xpaymentadapterapp.api.model.CreateChargeRequest;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateChargeRequestMapper {
    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto dto);
    CreateChargeRequestDto toCreateChargeRequestDto(CreateChargeRequest createChargeRequest);
}

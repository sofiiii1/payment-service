package com.iprody.payment.service.mapper;

import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Payment entity = new Payment();
        entity.setGuid(UUID.randomUUID());
        entity.setInquiryRefId(UUID.randomUUID());
        entity.setAmount(BigDecimal.valueOf(123.45));
        entity.setCurrency("USD");
        entity.setTransactionRefId(UUID.randomUUID());
        entity.setStatus(PaymentStatus.APPROVED);
        entity.setNote("Payment for invoice");
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        //when
        PaymentDto dto = paymentMapper.toDto(entity);

        //then
        assertNotNull(dto);
        assertEquals(entity.getGuid(), dto.getGuid());
        assertEquals(entity.getInquiryRefId(), dto.getInquiryRefId());
        assertEquals(entity.getAmount(), dto.getAmount());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getTransactionRefId(), dto.getTransactionRefId());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getNote(), dto.getNote());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.randomUUID());
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setAmount(BigDecimal.valueOf(999.99));
        dto.setCurrency("EUR");
        dto.setTransactionRefId(UUID.randomUUID());
        dto.setStatus(PaymentStatus.PENDING);
        dto.setNote("Payment for invoice");
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());

        //when
        Payment entity = paymentMapper.toEntity(dto);

        //then
        assertNotNull(entity);
        assertEquals(dto.getGuid(), entity.getGuid());
        assertEquals(dto.getInquiryRefId(), entity.getInquiryRefId());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getTransactionRefId(), entity.getTransactionRefId());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getNote(), entity.getNote());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void shouldMapToDtoWithNullFields() {
        //given
        Payment entity = new Payment();
        entity.setGuid(null);
        entity.setInquiryRefId(null);
        entity.setAmount(null);
        entity.setCurrency(null);
        entity.setTransactionRefId(null);
        entity.setStatus(null);
        entity.setNote(null);
        entity.setCreatedAt(null);
        entity.setUpdatedAt(null);

        //when
        PaymentDto dto = paymentMapper.toDto(entity);

        //then
        assertNotNull(dto);
        assertNull(dto.getGuid());
        assertNull(dto.getInquiryRefId());
        assertNull(dto.getAmount());
        assertNull(dto.getCurrency());
        assertNull(dto.getTransactionRefId());
        assertNull(dto.getStatus());
        assertNull(dto.getNote());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void shouldMapToEntityWithNullFields() {
        //given
        PaymentDto dto = new PaymentDto();
        dto.setGuid(null);
        dto.setInquiryRefId(null);
        dto.setAmount(null);
        dto.setCurrency(null);
        dto.setTransactionRefId(null);
        dto.setStatus(null);
        dto.setNote(null);
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);

        //when
        Payment entity = paymentMapper.toEntity(dto);

        //then
        assertNotNull(entity);
        assertNull(entity.getGuid());
        assertNull(entity.getInquiryRefId());
        assertNull(entity.getAmount());
        assertNull(entity.getCurrency());
        assertNull(entity.getTransactionRefId());
        assertNull(entity.getStatus());
        assertNull(entity.getNote());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void shouldMapToDtoNullInput() {
        //given
        Payment payment = null;

        //when
        PaymentDto dto = paymentMapper.toDto(payment);

        //then
        assertNull(dto);
    }

    @Test
    void shouldMapToEntityNullInput() {
        //given
        PaymentDto paymentDto = null;

        //when
        Payment entity = paymentMapper.toEntity(paymentDto);

        //then
        assertNull(entity);
    }
}

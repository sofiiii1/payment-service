package com.iprody.payment.service.service;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.exception.NotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        //given
        guid = UUID.randomUUID();
        payment = new Payment();
        payment.setGuid(guid);

        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setStatus(payment.getStatus());
    }

    @Test
    void shouldReturnPaymentById() {
        //when
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        PaymentDto result = paymentService.findById(guid);

        //then
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        //given
        payment.setStatus(status);
        paymentDto.setStatus(status);

        //when
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        PaymentDto result = paymentService.findById(guid);

        //then
        assertEquals(status, result.getStatus());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.RECEIVED,
                PaymentStatus.PENDING,
                PaymentStatus.APPROVED,
                PaymentStatus.DECLINED,
                PaymentStatus.NOT_SENT
        );
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        //when
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        //then
        Exception exception = assertThrows(NotFoundException.class, () -> paymentService.findById(guid));
        assertEquals("Payment not found", exception.getMessage());
        verify(paymentRepository).findById(guid);
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void shouldReturnEmptyListIfNoPaymentsByStatus() {
        //when
        when(paymentRepository.findAllByStatus(PaymentStatus.DECLINED)).thenReturn(Collections.emptyList());

        List<PaymentDto> result = paymentService.findByStatus(PaymentStatus.DECLINED);

        //then
        assertTrue(result.isEmpty());
        verify(paymentRepository).findAllByStatus(PaymentStatus.DECLINED);
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void shouldReturnPaginatedPayments() {
        //given
        PaymentFilterDto filterDto = new PaymentFilterDto();
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);

        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filterDto, page, size);

        //then
        assertEquals(1, result.getTotalElements());
        assertEquals(paymentDto.getGuid(), result.getContent().getFirst().getGuid());
        verify(paymentRepository).findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldReturnEmptyPageIfNoPaymentsFound() {
        //given
        PaymentFilterDto filterDto = new PaymentFilterDto();
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<PaymentDto> result = paymentService.findAll(filterDto, page, size);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository).findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable));
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void shouldFilterByCurrency() {
        //given
        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setCurrency("USD");
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filter, page, size);

        //then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByMinAmount() {
        //given
        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setMinAmount(new BigDecimal("50.00"));
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filter, page, size);

        //then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByMaxAmount() {
        //given
        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setMaxAmount(new BigDecimal("200.00"));
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filter, page, size);

        //then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByCreatedAfter() {
        //given
        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setCreatedAtAfter(Instant.now());
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filter, page, size);

        //then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByCreatedBefore() {
        //given
        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setCreatedAtBefore(Instant.now());
        int page = 0;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        //when
        when(paymentRepository.findAll(ArgumentMatchers.<Specification<Payment>>any(), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.findAll(filter, page, size);

        //then
        assertEquals(1, result.getTotalElements());
    }
}

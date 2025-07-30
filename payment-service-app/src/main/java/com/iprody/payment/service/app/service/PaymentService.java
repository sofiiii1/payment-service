package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public Page<PaymentDto> findAll(PaymentFilterDto paymentFilterDto, int page, int size) {
        return paymentRepository.findAll(
                PaymentFilterFactory.fromFilter(paymentFilterDto),
                PageRequest.of(page, size))
                .map(paymentMapper::toDto);
    }

    public PaymentDto findById(UUID guid) {
        return paymentRepository.findById(guid)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public List<PaymentDto> findByStatus(PaymentStatus status) {
        return paymentRepository.findAllByStatus(status)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }
}

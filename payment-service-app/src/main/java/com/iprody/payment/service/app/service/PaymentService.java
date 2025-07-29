package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Page<Payment> findAll(PaymentFilterDto paymentFilterDto, int page, int size) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilterDto), PageRequest.of(page, size));
    }

    public Payment findById (UUID guid) {
        return paymentRepository.findById(guid).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findAllByStatus(status);
    }
}

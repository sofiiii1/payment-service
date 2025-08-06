package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Page<PaymentDto> findAll(PaymentFilterDto filter, int page, int size);
    PaymentDto findById(UUID guid);
    List<PaymentDto> findByStatus(PaymentStatus status);
}

package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.exception.NotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

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
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    public List<PaymentDto> findByStatus(PaymentStatus status) {
        return paymentRepository.findAllByStatus(status)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    public PaymentDto create(PaymentDto dto) {
        final Payment entity = paymentMapper.toEntity(dto);
        entity.setGuid(UUID.randomUUID());
        final Payment saved = paymentRepository.save(entity);
        return paymentMapper.toDto(saved);
    }

    public PaymentDto update(UUID id, PaymentDto dto) {
        paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Payment not found" + id));
        final Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(id);
        final Payment saved = paymentRepository.save(updated);
        return paymentMapper.toDto(saved);
    }

    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException("Payment not found" + id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Payment not found" + id));
        payment.setStatus(status);
        final Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    @Override
    public PaymentDto updateNote(UUID id, String note) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Payment not found" + id));
        payment.setNote(note);
        final Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }
}

package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/all")
    public Page<PaymentDto> search(@ModelAttribute PaymentFilterDto filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size) {
        return this.paymentService.findAll(filter, page, size);
    }

    @GetMapping("/{guid}")
    public PaymentDto getPaymentById(@PathVariable UUID guid) {
        return this.paymentService.findById(guid);
    }

    @GetMapping("/by_status/{status}")
    public List<PaymentDto> getByStatus(@PathVariable PaymentStatus status) {
        return paymentService.findByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@RequestBody PaymentDto dto) {
        return paymentService.create(dto);
    }

    @PutMapping("/{id}")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return paymentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/status")
    public PaymentDto updateStatus(
        @PathVariable UUID id,
        @RequestBody PaymentStatusUpdateDto dto) {
        return paymentService.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    public PaymentDto updateStatus(
        @PathVariable UUID id,
        @RequestBody PaymentNoteUpdateDto dto) {
        return paymentService.updateNote(id, dto.getNote());
    }
}

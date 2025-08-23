package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> search(@ModelAttribute PaymentFilterDto filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size) {
        log.info("GET payments by filters: {} {} {}", filter, page, size);
        final Page<PaymentDto> pageResponse = this.paymentService.findAll(filter, page, size);
        log.debug("Sending response with page of PaymentDto: {}", pageResponse);
        return pageResponse;
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPaymentById(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        final PaymentDto paymentDto = this.paymentService.findById(guid);
        log.debug("Sending response with PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @GetMapping("/by_status/{status}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getByStatus(@PathVariable PaymentStatus status) {
        log.info("GET payments by status: {}", status);
        final List<PaymentDto> paymentDtoList = paymentService.findByStatus(status);
        log.debug("Sending response with list of PaymentDto: {}", paymentDtoList);
        return paymentDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody PaymentDto dto) {
        log.info("POST payment with id: {}", dto.getGuid());
        final PaymentDto paymentDto = paymentService.create(dto);
        log.debug("Sending response with created PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        log.info("PUT payment with id: {}", dto.getGuid());
        final PaymentDto paymentDto = paymentService.update(id, dto);
        log.debug("Sending response with updated PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("DELETE payment with id: {}", id);
        log.debug("Delete PaymentDto with id: {}", id);
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateStatus(
        @PathVariable UUID id,
        @RequestBody PaymentStatusUpdateDto dto) {
        log.info("PATCH status {} of payment with id: {}", dto.getStatus(), id);
        final PaymentDto paymentDto = paymentService.updateStatus(id, dto.getStatus());
        log.debug("Sending response with updated status PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateStatus(
        @PathVariable UUID id,
        @RequestBody PaymentNoteUpdateDto dto) {
        log.info("PATCH note {} of payment with id: {}", dto.getNote(), id);
        final PaymentDto paymentDto = paymentService.updateNote(id, dto.getNote());
        log.debug("Sending response with updated note PaymentDto: {}", paymentDto);
        return paymentDto;
    }
}

package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.service.PaymentDto;
import com.iprody.payment.service.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/all")
    public Page<PaymentDto> getPayments(@ModelAttribute PaymentFilterDto filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "9") int size) {
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
}

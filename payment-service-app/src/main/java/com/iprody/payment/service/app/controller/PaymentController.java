package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final Payment payment = new Payment(1L, 99.99);
    private final Map<Long, Payment> payments = new HashMap<>();

    public PaymentController() {
        payments.put(1L, new Payment(1L, 99.99));
        payments.put(2L, new Payment(2L, 99.99));
        payments.put(3L, new Payment(3L, 99.99));
        payments.put(4L, new Payment(4L, 99.99));
        payments.put(5L, new Payment(5L, 99.99));
    }

    @GetMapping("/payment")
    public Payment getPayment() {
        return payment;
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return payments.get(id);
    }

    @GetMapping
    public List<Payment> getPayments() {
        return payments.values().stream().toList();
    }
}

package com.iprody.payment.service.app.persistency;

import com.iprody.payment.service.app.controller.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.entity.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PaymentFilterFactory {

    private static final Specification<Payment> EMPTY = ((root, query, criteriaBuilder) -> null);

    public static Specification<Payment> fromFilter(PaymentFilterDto filter) {

        Specification<Payment> spec = EMPTY;
        if (filter.getStatus() != null) {
            spec = spec.and(PaymentSpecification.hasStatus(filter.getStatus()));
        }

        if (StringUtils.hasText(filter.getCurrency())) {
            spec = spec.and(PaymentSpecification.hasCurrency(filter.getCurrency()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecification.amountBetween(filter.getMinAmount(), filter.getMaxAmount()));
        }

        if (filter.getCreatedAtAfter() != null && filter.getCreatedAtBefore() != null) {
            spec = spec.and(PaymentSpecification.createdBetween(filter.getCreatedAtAfter(),
                    filter.getCreatedAtBefore()));
        }

        return spec;
    }
}

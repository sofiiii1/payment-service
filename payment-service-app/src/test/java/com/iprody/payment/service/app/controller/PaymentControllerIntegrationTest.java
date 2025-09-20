package com.iprody.payment.service.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iprody.payment.service.app.AbstractIntegrationTest;
import com.iprody.payment.service.app.TestJwtFactory;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        mockMvc.perform(get("/payments/all")
                        .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists());
    }

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setInquiryRefId(UUID.randomUUID());
        String json = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(post("/payments")
                .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response,
                PaymentDto.class);
        Optional<Payment> saved =
                paymentRepository.findById(created.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        UUID existingId =
                UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void shouldReturn404ForNonexistentPayment() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get("/payments/" + nonexistentId)
                        .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Payment not found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation")
                        .value("findById"))
                .andExpect(jsonPath("$.entityId")
                        .value(nonexistentId.toString()));
    }

    @Test
    void shouldReturn403ForUnauthorizedRoleWhenCreatePayment() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setInquiryRefId(UUID.randomUUID());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("test-user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void shouldReturnPaymentByStatus() throws Exception {
        PaymentStatus existingStatus = PaymentStatus.APPROVED;

        mockMvc.perform(get("/payments/by_status/" + existingStatus)
                        .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists());
    }

    @Test
    void shouldUpdatePaymentAndVerifyInDatabase() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setInquiryRefId(UUID.randomUUID());
        String json = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(put("/payments/" + dto.getGuid())
                        .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.status").value(PaymentStatus.PENDING.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response,
                PaymentDto.class);
        Optional<Payment> updated =
                paymentRepository.findById(created.getGuid());
        assertThat(updated).isPresent();
        assertThat(updated.get().getCurrency()).isEqualTo("EUR");
        assertThat(updated.get().getAmount()).isEqualByComparingTo("123.45");
        assertThat(updated.get().getStatus()).isEqualByComparingTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldReturn403ForUnauthorizedRoleWhenUpdatePayment() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setInquiryRefId(UUID.randomUUID());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(put("/payments/" + dto.getGuid())
                        .with(TestJwtFactory.jwtWithRole("test-user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void shouldUpdateStatusAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PaymentStatusUpdateDto dto = new PaymentStatusUpdateDto();
        dto.setStatus(PaymentStatus.PENDING);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch("/payments/" + existingId + "/status")
                        .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.status").value(PaymentStatus.PENDING.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Payment> updated =
                paymentRepository.findById(existingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualByComparingTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldUpdateNoteAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PaymentNoteUpdateDto dto = new PaymentNoteUpdateDto();
        dto.setNote("New note");
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch("/payments/" + existingId + "/note")
                        .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.note").value(dto.getNote()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Payment> updated =
                paymentRepository.findById(existingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getNote()).isEqualTo(dto.getNote());
    }

    @Test
    void shouldDeletePaymentAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        mockMvc.perform(delete("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.guid").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Payment> deleted =
                paymentRepository.findById(existingId);
        assertThat(deleted).isNotPresent();
    }

    @Test
    void shouldReturn500WhenWrongRequest() throws Exception {
        mockMvc.perform(delete("/payments/all")
                        .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorCode").value(500));
    }
}

package com.iprody.xpaymentadapterapp.async.kafka;

import com.iprody.async.AsyncSender;
import com.iprody.async.XPaymentAdapterRequestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterSender implements AsyncSender<XPaymentAdapterRequestMessage> {

    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> kafkaTemplate;
    private final String topic;

    @Autowired
    public DeadLetterSender(KafkaTemplate<String, XPaymentAdapterRequestMessage> kafkaTemplate,
        @Value("${app.kafka.topics.xpayment-adapter.dlt}") String topic ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage message) {
        final String key = message.getPaymentGuid().toString();
        kafkaTemplate.send(topic, key, message);
    }
}

package com.iprody.xpaymentadapterapp.async.kafka;


import com.iprody.async.AsyncSender;
import com.iprody.async.XPaymentAdapterResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class KafkaXPaymentAdapterResponseSender implements AsyncSender<XPaymentAdapterResponseMessage> {

    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;
    private final String topic;

    public KafkaXPaymentAdapterResponseSender(KafkaTemplate<String, XPaymentAdapterResponseMessage> template,
        @Value("${app.kafka.topics.xpayment-adapter.response}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterResponseMessage msg) {
        final String key = msg.getPaymentGuid().toString();
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency={} -> topic={}", msg.getPaymentGuid(),
            msg.getAmount(), msg.getCurrency(), topic);
        template.send(topic, key, msg);
    }
}

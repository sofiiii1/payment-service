package com.iprody.xpaymentadapterapp.async.kafka;

import com.iprody.async.AsyncListener;
import com.iprody.async.MessageHandler;
import com.iprody.async.XPaymentAdapterRequestMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {

    private final MessageHandler<XPaymentAdapterRequestMessage> handler;
    public KafkaXPaymentAdapterRequestListenerAdapter(MessageHandler<XPaymentAdapterRequestMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.xpayment-adapter.request}",
        groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterRequestMessage message, ConsumerRecord<String,
        XPaymentAdapterRequestMessage> record, Acknowledgment ack) {
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid={}", message.getPaymentGuid(), e);
            throw e;
        }
    }
}

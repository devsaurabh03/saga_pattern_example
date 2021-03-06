package com.sample.paymentservice.eventhandler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.sample.paymentservice.event.RefundPaymentEvent;
import com.sample.paymentservice.service.PaymentService;
import com.sample.paymentservice.util.Converter;
import com.sample.paymentservice.util.TransactionIdHolder;

@Log4j2
@Component
@AllArgsConstructor
public class RefundPaymentHandler {

    private final Converter converter;
    private final PaymentService paymentService;
    private final TransactionIdHolder transactionIdHolder;

    @RabbitListener(queues = {"${queue.refund-payment}"})
    public void onRefundPayment(@Payload String payload) {
    	
        log.debug("Handling a refund order event {}", payload);
        
        RefundPaymentEvent event = converter.toObject(payload, RefundPaymentEvent.class);
        
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        
        paymentService.refund(event.getOrder().getId());
        
    }
}
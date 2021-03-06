package com.sample.paymentservice.eventhandler;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.sample.paymentservice.event.OrderCreatedEvent;
import com.sample.paymentservice.service.PaymentService;
import com.sample.paymentservice.util.Converter;
import com.sample.paymentservice.util.TransactionIdHolder;

@Log4j2
@Component
@AllArgsConstructor
public class OrderCreateHandler {

    private final Converter converter;
    private final PaymentService paymentService;
    private final TransactionIdHolder transactionIdHolder;

    @RabbitListener(queues = {"${queue.order-create}"})
    public void handle(@Payload String payload) {
    	
        log.debug("Handling a created order event {}", payload);
        
        OrderCreatedEvent event = converter.toObject(payload, OrderCreatedEvent.class);
        
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        
        paymentService.charge(event.getOrder());
        
    }
}
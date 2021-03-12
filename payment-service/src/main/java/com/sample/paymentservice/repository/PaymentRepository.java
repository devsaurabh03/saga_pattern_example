package com.sample.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.paymentservice.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	
    Optional<Payment> findByOrderId(Long orderId);
    
}
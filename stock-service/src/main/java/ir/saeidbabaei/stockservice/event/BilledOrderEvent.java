package com.sample.stockservice.event;

import com.sample.stockservice.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BilledOrderEvent {
	
	private String transactionId;
    
    private Order order;
    
}
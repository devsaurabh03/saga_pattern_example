package com.sample.stockservice.service;

import com.sample.stockservice.event.OrderCanceledEvent;
import com.sample.stockservice.event.OrderDoneEvent;
import com.sample.stockservice.exception.StockException;
import com.sample.stockservice.model.Order;
import com.sample.stockservice.model.Product;
import com.sample.stockservice.repository.ProductRepository;
import com.sample.stockservice.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class StockService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;
    private final TransactionIdHolder transactionIdHolder;

    @Transactional
    public void updateQuantity(Order order) {
    	
        log.debug("Start updating product {}", order.getProductId());

        Product product = getProduct(order);
        
        checkStock(order, product);
        
        updateStock(order, product);

        publishOrderDone(order);
        
    }

    private void updateStock(Order order, Product product) {
    	
        product.setQuantity(product.getQuantity() - order.getQuantity());
        
        log.debug("Updating product {} with quantity {}", product.getId(), product.getQuantity());
        
        productRepository.save(product);
        
    }

    private void publishOrderDone(Order order) {
    	
        OrderDoneEvent event = new OrderDoneEvent(transactionIdHolder.getCurrentTransactionId(), order);
        
        log.debug("Publishing order done event {}", event);
        
        publisher.publishEvent(event);
        
    }

    private void checkStock(Order order, Product product) {
    	
        log.debug("Checking, products available {}, products ordered {}", product.getQuantity(), order.getQuantity());
        
        if (product.getQuantity() < order.getQuantity()) {
        	
            publishCanceledOrder(order);
            
            throw new StockException("Product " + product.getId() + " is out of stock");
            
        }
    }

    private void publishCanceledOrder(Order order) {
    	
        OrderCanceledEvent event = new OrderCanceledEvent(transactionIdHolder.getCurrentTransactionId(), order);
        
        log.debug("Publishing canceled order event {}", event);
        
        publisher.publishEvent(event);
        
    }

    private Product getProduct(Order order) {
    	
        Optional<Product> optionalProduct = productRepository.findById(order.getProductId());
        
        return optionalProduct.orElseThrow(() -> {
        	
            publishCanceledOrder(order);
            
            return new StockException("Cannot find a product " + order.getProductId());
            
        });
    }
}
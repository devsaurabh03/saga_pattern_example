package com.sample.stockservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.stockservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
}
package com.buoi4.baoduy.repository;

import com.buoi4.baoduy.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
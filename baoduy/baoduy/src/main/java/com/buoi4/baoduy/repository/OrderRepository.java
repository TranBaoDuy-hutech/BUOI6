package com.buoi4.baoduy.repository;

import com.buoi4.baoduy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Integer> {}
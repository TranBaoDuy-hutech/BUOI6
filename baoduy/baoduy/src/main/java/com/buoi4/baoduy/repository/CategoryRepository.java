package com.buoi4.baoduy.repository;

import com.buoi4.baoduy.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
package com.buoi4.baoduy.service;

import com.buoi4.baoduy.model.Product;
import com.buoi4.baoduy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<Product> getAll() {
        return repo.findAll();
    }

    public void add(Product p) {
        repo.save(p);
    }

    public Product findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public void update(Product p) {
        repo.save(p);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
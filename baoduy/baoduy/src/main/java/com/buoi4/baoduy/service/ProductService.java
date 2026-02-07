package com.buoi4.baoduy.service;

import com.buoi4.baoduy.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private int AUTO_ID = 1;

    @PostConstruct
    public void initData() {

        Product p1 = new Product();
        p1.setId(AUTO_ID++);
        p1.setName("iPhone 17 Pro Max");
        p1.setPrice(35000);
        p1.setImage("17.jpg");   // nhớ thêm ảnh này
        p1.setCategoryId(2);

        Product p2 = new Product();
        p2.setId(AUTO_ID++);
        p2.setName("iPhone 16 Pro Max");
        p2.setPrice(30000);
        p2.setImage("16.jpg");   // nhớ thêm ảnh này
        p2.setCategoryId(2);

        products.add(p1);
        products.add(p2);
    }

    public List<Product> getAll(){
        return products;
    }

    public void add(Product p){
        p.setId(AUTO_ID++);
        products.add(p);
    }

    public Product findById(Integer id){
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void update(Product p){
        Product old = findById(p.getId());
        if(old != null){
            old.setName(p.getName());
            old.setPrice(p.getPrice());
            old.setImage(p.getImage());
            old.setCategoryId(p.getCategoryId());
        }
    }

    public void delete(Integer id){
        products.removeIf(p -> p.getId().equals(id));
    }
}

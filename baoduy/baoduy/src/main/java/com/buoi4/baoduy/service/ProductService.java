package com.buoi4.baoduy.service;

import com.buoi4.baoduy.model.Product;
import com.buoi4.baoduy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<Product> getAll() {
        return repo.findAll();
    }

    // Câu 1+2+3+4: search + page + sort + category
    public Page<Product> search(String keyword, Integer categoryId,
                                 String sortBy, int page) {
        // Câu 3: sort
        Sort sort = Sort.unsorted();
        if ("asc".equals(sortBy))  sort = Sort.by("price").ascending();
        if ("desc".equals(sortBy)) sort = Sort.by("price").descending();

        // Câu 2: pagination - 5 sản phẩm/trang
        Pageable pageable = PageRequest.of(page, 5, sort);

        String kw = (keyword == null) ? "" : keyword.trim();

        // Câu 1+4: kết hợp search + filter
        if (categoryId != null && categoryId > 0) {
            return repo.findByNameContainingIgnoreCaseAndCategoryId(kw, categoryId, pageable);
        }
        return repo.findByNameContainingIgnoreCase(kw, pageable);
    }

    public void add(Product p)            { repo.save(p); }
    public Product findById(Integer id)   { return repo.findById(id).orElse(null); }
    public void update(Product p)         { repo.save(p); }
    public void delete(Integer id)        { repo.deleteById(id); }
}
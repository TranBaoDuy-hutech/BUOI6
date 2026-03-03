package com.buoi4.baoduy.controller;

import com.buoi4.baoduy.model.Category;
import com.buoi4.baoduy.model.Product;
import com.buoi4.baoduy.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    // Đường dẫn upload chuẩn khi chạy Spring Boot
    private final Path UPLOAD_DIR =
            Paths.get(System.getProperty("user.dir"),
                    "src/main/resources/static/images");

    private final List<Category> categories = List.of(
            new Category(1, "Laptop"),
            new Category(2, "Điện thoại")
    );

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.getAll());
        return "product/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        return "product/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Product product,
                       BindingResult result,
                       @RequestParam("file") MultipartFile file,
                       Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("categories", categories);
            return "product/form";
        }

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR);
        }

        // Upload ảnh
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = UPLOAD_DIR.resolve(fileName);

            Files.copy(file.getInputStream(), filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            product.setImage(fileName);
        }

        if (product.getId() == null) {
            service.add(product);
        } else {
            service.update(product);
        }

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        Product product = service.findById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/products";
    }
}
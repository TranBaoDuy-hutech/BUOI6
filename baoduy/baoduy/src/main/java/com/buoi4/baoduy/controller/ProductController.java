package com.buoi4.baoduy.controller;

import com.buoi4.baoduy.model.Category;
import com.buoi4.baoduy.model.Product;
import com.buoi4.baoduy.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class ProductController {

    @Autowired
    private ProductService service;

    private final Path UPLOAD_DIR =
            Paths.get(System.getProperty("user.dir"),
                    "src/main/resources/static/images");

    private final List<Category> categories = List.of(
            new Category(1, "Laptop"),
            new Category(2, "Điện thoại")
    );

    // ── Trang Admin: có đầy đủ chức năng ──────────────────────────────────
    @GetMapping("/products")
    public String list(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("products", service.getAll());

        if (principal != null) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        } else {
            model.addAttribute("username", null);
            model.addAttribute("isAdmin", false);
        }

        return "product/list";
    }

    // ── Trang User: chỉ xem sản phẩm ──────────────────────────────────────
    @GetMapping("/shop")
    public String shop(Model model, Principal principal) {
        model.addAttribute("products", service.getAll());

        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "product/list-user";
    }

    // ── Tạo sản phẩm ──────────────────────────────────────────────────────
    @GetMapping("/products/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        return "product/form";
    }

    // ── Lưu sản phẩm ──────────────────────────────────────────────────────
    @PostMapping("/products/save")
    public String save(@Valid @ModelAttribute Product product,
                       BindingResult result,
                       @RequestParam("file") MultipartFile file,
                       Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("categories", categories);
            return "product/form";
        }

        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR);
        }

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = UPLOAD_DIR.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImage(fileName);
        }

        if (product.getId() == null) {
            service.add(product);
        } else {
            service.update(product);
        }

        return "redirect:/products";
    }

    // ── Sửa sản phẩm ──────────────────────────────────────────────────────
    @GetMapping("/products/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product product = service.findById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/form";
    }

    // ── Xóa sản phẩm ──────────────────────────────────────────────────────
    @GetMapping("/products/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/products";
    }
}
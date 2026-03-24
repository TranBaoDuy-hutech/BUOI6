package com.buoi4.baoduy.controller;

import com.buoi4.baoduy.model.*;
import com.buoi4.baoduy.repository.OrderDetailRepository;
import com.buoi4.baoduy.repository.OrderRepository;
import com.buoi4.baoduy.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;

@Controller
public class ProductController {

    @Autowired private ProductService service;
    @Autowired private OrderRepository orderRepo;
    @Autowired private OrderDetailRepository orderDetailRepo;

    private final Path UPLOAD_DIR = Paths.get(
            System.getProperty("user.dir"), "src/main/resources/static/images");

    private final List<Category> categories = List.of(
            new Category(1, "Laptop"),
            new Category(2, "Điện thoại")
    );

    // ── Câu 1+2+3+4: list với search, sort, page, filter ─────────────────
    @GetMapping("/products")
public String list(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") Integer categoryId,
        @RequestParam(defaultValue = "") String sortBy,
        @RequestParam(defaultValue = "0") int page,
        Model model, Principal principal, HttpServletRequest request) {

    Page<Product> productPage = service.search(keyword, categoryId, sortBy, page);

    model.addAttribute("products",   productPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages",  productPage.getTotalPages());
    model.addAttribute("keyword",     keyword);
    model.addAttribute("categoryId",  categoryId);
    model.addAttribute("sortBy",      sortBy);
    model.addAttribute("categories",  categories);

    if (principal != null) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
    } else {
        model.addAttribute("isAdmin", false);
    }

    return "product/list";
}

    // ── Câu 5: Thêm vào giỏ hàng ─────────────────────────────────────────
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        Product p = service.findById(productId);
        if (p == null) return "redirect:/products";

        Map<Integer, CartItem> cart = getCart(session);

        if (cart.containsKey(productId)) {
            cart.get(productId).setQuantity(cart.get(productId).getQuantity() + quantity);
        } else {
            cart.put(productId, new CartItem(
                    p.getId(), p.getName(), p.getPrice(), p.getImage(), quantity));
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ── Câu 6: Xem giỏ hàng ──────────────────────────────────────────────
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = getCart(session);
        double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("total", total);
        return "product/cart";
    }

    // Xóa khỏi giỏ
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Integer id, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        cart.remove(id);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ── Câu 7: Trang checkout ─────────────────────────────────────────────
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";
        double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("total", total);
        model.addAttribute("order", new Order());
        return "product/checkout";
    }

    // Câu 7: Đặt hàng
    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute Order order, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
        order.setTotalAmount(total);
        Order saved = orderRepo.save(order);

        for (CartItem item : cart.values()) {
            OrderDetail detail = new OrderDetail(saved, item);
            orderDetailRepo.save(detail);
        }

        session.removeAttribute("cart");
        return "redirect:/order-success?id=" + saved.getId();
    }

    @GetMapping("/order-success")
    public String orderSuccess(@RequestParam Integer id, Model model) {
        model.addAttribute("orderId", id);
        return "product/order-success";
    }

    // ── Helper ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpSession session) {
        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new LinkedHashMap<>();
        return cart;
    }

    // ── CRUD giữ nguyên ───────────────────────────────────────────────────
    @GetMapping("/shop")
    public String shop(Model model, Principal principal) {
        model.addAttribute("products", service.getAll());
        if (principal != null) model.addAttribute("username", principal.getName());
        return "product/list-user";
    }

    @GetMapping("/products/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        return "product/form";
    }

    @PostMapping("/products/save")
    public String save(@Valid @ModelAttribute Product product,
                       BindingResult result,
                       @RequestParam("file") MultipartFile file,
                       Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("categories", categories);
            return "product/form";
        }
        if (!Files.exists(UPLOAD_DIR)) Files.createDirectories(UPLOAD_DIR);
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(),
                    UPLOAD_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            product.setImage(fileName);
        }
        if (product.getId() == null) service.add(product);
        else service.update(product);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product p = service.findById(id);
        if (p == null) return "redirect:/products";
        model.addAttribute("product", p);
        model.addAttribute("categories", categories);
        return "product/form";
    }

    @GetMapping("/products/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/products";
    }
}
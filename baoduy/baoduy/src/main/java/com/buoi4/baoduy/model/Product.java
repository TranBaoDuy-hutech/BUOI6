package com.buoi4.baoduy.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Product {

    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Min(value = 1, message = "Giá từ 1 → 999999")
    @Max(value = 999999, message = "Giá từ 1 → 999999")
    private Integer price;

    @Size(max = 200, message = "Tên hình ảnh không quá 200 ký tự")
    private String image;

    private Integer categoryId;

    public Product() {
    }

    // Getter & Setter đầy đủ

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}

package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.ProductForm;
import com.example.demo.model.CategoryType;
import com.example.demo.model.Product;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;

@Controller
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @ModelAttribute("categories")
    public List<CategoryType> categories() {
        return Arrays.asList(CategoryType.values());
    }

    @GetMapping({"/", "/products"})
    public String showProducts(Model model) {
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductForm());
        }
        if (!model.containsAttribute("isEdit")) {
            model.addAttribute("isEdit", false);
        }
        model.addAttribute("products", productService.findAll());
        return "products";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (form.getImageFile() == null || form.getImageFile().isEmpty()) {
            bindingResult.rejectValue("imageFile", "required", "Vui lòng chọn hình ảnh");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("products", productService.findAll());
            model.addAttribute("isEdit", false);
            return "products";
        }

        String imageUrl = fileStorageService.store(form.getImageFile());
        Product product = new Product(null, form.getName(), form.getPrice(), imageUrl, form.getCategory());
        productService.create(product);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
            return "redirect:/products";
        }

        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setPrice(product.getPrice());
        form.setCategory(product.getCategory());
        form.setExistingImageUrl(product.getImageUrl());

        model.addAttribute("productForm", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("products", productService.findAll());
        return "products";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") ProductForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Product existing = productService.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
            return "redirect:/products";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("products", productService.findAll());
            model.addAttribute("isEdit", true);
            form.setExistingImageUrl(existing.getImageUrl());
            return "products";
        }

        String imageUrl = existing.getImageUrl();
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            imageUrl = fileStorageService.store(form.getImageFile());
        }

        Product updatedProduct = new Product(id, form.getName(), form.getPrice(), imageUrl, form.getCategory());
        productService.update(id, updatedProduct);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công");
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công");
        return "redirect:/products";
    }
}

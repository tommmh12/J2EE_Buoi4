package com.example.demo.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.demo.model.Product;

@Service
public class InMemoryProductService implements ProductService {
    private final Map<Long, Product> products = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<Product> findAll() {
        return products.values()
                .stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Product create(Product product) {
        long nextId = idGenerator.incrementAndGet();
        product.setId(nextId);
        products.put(nextId, product);
        return product;
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = products.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id);
        }

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setImageUrl(product.getImageUrl());
        return existing;
    }

    @Override
    public void delete(Long id) {
        products.remove(id);
    }
}

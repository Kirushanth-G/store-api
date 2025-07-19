package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam(required = false, name = "categoryId") Byte categoryId) {
        var products = (categoryId != null)
                ? productRepository.findByCategoryId(categoryId)
                : productRepository.findAllWithCategory();
        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto request,
            UriComponentsBuilder uriBuilder) {

        if (request.getId() != null && request.getId() == 0L) {
            request.setId(null);
        }
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);
        request.setId(product.getId());

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(request.getId()).toUri();

        return ResponseEntity.created(uri).body(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto request){
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        productMapper.update(request, product);
        product.setCategory(category);
        productRepository.save(product);
        request.setId(product.getId());

        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}

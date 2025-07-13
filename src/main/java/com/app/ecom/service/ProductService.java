package com.app.ecom.service;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;
import com.app.ecom.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapProductToProductResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> fetchProduct(Long id) {
        return productRepository.findById(id).map(this::mapProductToProductResponse);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(product -> {
            updateExistingProduct(product, productRequest);
            if(productRequest.getStockQuantity() == 0){
                product.setActive(false);
            }
            return productRepository.save(product);
        }).map(this::mapProductToProductResponse);
    }

    private static void updateExistingProduct(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        return mapProductToProductResponse(productRepository.save(mapProductRequestToProduct(productRequest)));
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapProductToProductResponse)
                .collect(Collectors.toList());
    }

    private Product mapProductRequestToProduct(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .category(productRequest.getCategory())
                .imageUrl(productRequest.getImageUrl())
                .active(true)
                .build();
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .build();
    }
}

package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.exception.InsufficientStockException;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItem> fetchItems(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return cartItemRepository.findCartItemsByUser(user);
    }

    public void addToCart(String userId, CartItemRequest cartItemRequest) {
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build()
                );

        int totalQuantity = cartItem.getQuantity() + cartItemRequest.getQuantity();
        if (product.getStockQuantity() < totalQuantity) {
            throw new InsufficientStockException("Not enough stock for product");
        }

        cartItem.setQuantity(totalQuantity);
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(totalQuantity)));

        cartItemRepository.save(cartItem);
    }

    public void deleteItemFromCart(String userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found"));

        cartItemRepository.delete(cartItem);
    }
}

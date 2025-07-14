package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String X_USER_ID = "X-User-ID";

    private final CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCart(
            @RequestHeader(X_USER_ID) String userId
    ){
        return ResponseEntity.ok(cartService.fetchItems(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addToCart(
            @RequestHeader(X_USER_ID) String userId,
            @RequestBody CartItemRequest request) {
        cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader(X_USER_ID) String userId,
            @PathVariable Long productId) {
        cartService.deleteItemFromCart(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

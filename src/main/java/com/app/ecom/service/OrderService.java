package com.app.ecom.service;

import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.model.*;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderResponse createOrder(String userId){
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findCartItemsByUser(user);

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> mapFromCartItem(cartItem, order))
                .toList();
        order.setItems(orderItems);

        OrderResponse savedOrder = mapFromOrder(orderRepository.save(order));

        cartItemRepository.deleteByUser(user);

        return savedOrder;
    }

    private OrderItem mapFromCartItem(CartItem cartItem, Order order){
        return OrderItem.builder()
                .product(cartItem.getProduct())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .order(order)
                .build();
    }

    private OrderResponse mapFromOrder(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream().map(this::mapFromOrderItem).toList())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemDTO mapFromOrderItem(OrderItem orderItem){
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getProduct().getPrice())
                .build();
    }
}

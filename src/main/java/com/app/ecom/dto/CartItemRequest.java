package com.app.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}

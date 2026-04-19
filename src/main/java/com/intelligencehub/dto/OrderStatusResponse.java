package com.intelligencehub.dto;

import java.time.LocalDateTime;

public record OrderStatusResponse(
        boolean found,
        String orderNumber,
        Long customerId,
        String customerName,
        String productName,
        Integer quantity,
        Double totalPrice,
        String status,
        String shippingAddress,
        LocalDateTime createdAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        String errorMessage) {
}
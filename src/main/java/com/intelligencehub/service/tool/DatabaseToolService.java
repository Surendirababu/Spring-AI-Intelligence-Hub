package com.intelligencehub.service.tool;

import com.intelligencehub.entity.Order;
import com.intelligencehub.entity.Product;
import com.intelligencehub.repository.OrderRepository;
import com.intelligencehub.repository.ProductRepository;
import com.intelligencehub.dto.OrderStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * DatabaseToolService - Tools for querying structured SQL data
 */
@Slf4j
@Service
public class DatabaseToolService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DatabaseToolService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderStatusResponse findOrderStatus(String orderNumber) {
        log.info("Tool called: findOrderStatus({})", orderNumber);

        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);

        if (order.isEmpty()) {
            return new OrderStatusResponse(
                false, null, null, null, null, null, null, null, null, null, null, null,
                "Order not found: " + orderNumber);
        }

        Order foundOrder = order.get();
        return new OrderStatusResponse(
            true,
            foundOrder.getOrderNumber(),
            foundOrder.getCustomer().getId(),
            foundOrder.getCustomer().getFirstName() + " " +
                     foundOrder.getCustomer().getLastName(),
            foundOrder.getProduct().getName(),
            foundOrder.getQuantity(),
            foundOrder.getTotalPrice(),
            foundOrder.getStatus().toString(),
            foundOrder.getShippingAddress(),
            foundOrder.getCreatedAt(),
            foundOrder.getShippedAt(),
            foundOrder.getDeliveredAt(),
            null);
    }

    public String getProductSpecifications(String productId) {
        log.info("Tool called: getProductSpecifications({})", productId);

        Optional<Product> product = productRepository.findById(Long.parseLong(productId));

        if (product.isEmpty()) {
            return "Product not found with ID: " + productId;
        }

        Product p = product.get();
        return String.format(
            "Product: %s\nSKU: %s\nPrice: $%.2f\nStock: %d units\n" +
            "Description: %s\nSpecifications: %s",
            p.getName(), p.getSku(), p.getPrice(), p.getStockQuantity(),
            p.getDescription(), p.getSpecifications()
        );
    }

    public String searchProducts(String productName) {
        log.info("Tool called: searchProducts({})", productName);

        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);

        if (products.isEmpty()) {
            return "No products found matching: " + productName;
        }

        StringBuilder result = new StringBuilder();
        for (Product p : products) {
            result.append(String.format(
                "- %s (ID: %d, Price: $%.2f, In Stock: %d)\n",
                p.getName(), p.getId(), p.getPrice(), p.getStockQuantity()
            ));
        }

        return result.toString();
    }
}

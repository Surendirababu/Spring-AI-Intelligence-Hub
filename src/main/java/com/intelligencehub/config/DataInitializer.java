package com.intelligencehub.config;

import com.intelligencehub.entity.*;
import com.intelligencehub.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data
        if (customerRepository.count() == 0) {
            log.info("Initializing sample data...");

            // Create customers
            Customer customer1 = new Customer();
            customer1.setFirstName("Surendirababu");
            customer1.setLastName("Janarthanan");
            customer1.setEmail("babusuren@example.com");
            customer1.setPhone("9876543210");
            customer1.setAddress("Chennai");
            customerRepository.save(customer1);

            // Customer 2
            Customer customer2 = new Customer();
            customer2.setFirstName("Sai");
            customer2.setLastName("Thejas");
            customer2.setEmail("Sai.Thejas@example.com");
            customer2.setPhone("9123456780");
            customer2.setAddress("Bangalore");
            customerRepository.save(customer2);

            // Customer 3
            Customer customer3 = new Customer();
            customer3.setFirstName("Arun");
            customer3.setLastName("Kumar");
            customer3.setEmail("arun.kumar@example.com");
            customer3.setPhone("9988776655");
            customer3.setAddress("Hyderabad");
            customerRepository.save(customer3);

            // Customer 4
            Customer customer4 = new Customer();
            customer4.setFirstName("Meena");
            customer4.setLastName("Sridhar");
            customer4.setEmail("meena.sridhar@example.com");
            customer4.setPhone("9876501234");
            customer4.setAddress("Coimbatore");
            customerRepository.save(customer4);

            // Customer 5
            Customer customer5 = new Customer();
            customer5.setFirstName("Ravi");
            customer5.setLastName("Varma");
            customer5.setEmail("ravi.varma@example.com");
            customer5.setPhone("9765432109");
            customer5.setAddress("Mumbai");
            customerRepository.save(customer5);

            // Customer 6
            Customer customer6 = new Customer();
            customer6.setFirstName("Lakshmi");
            customer6.setLastName("Priya");
            customer6.setEmail("lakshmi.Prya@example.com");
            customer6.setPhone("9345678901");
            customer6.setAddress("Pune");
            customerRepository.save(customer6);

            // Create products
            Product product1 = new Product();
            product1.setName("Boat Wireless Headset");
            product1.setDescription("Premium wireless headphones with noise cancellation");
            product1.setPrice(299.99);
            product1.setStockQuantity(50);
            product1.setSku("WHP-001");
            product1.setSpecifications("Battery: 30h, Bluetooth 5.0, ANC enabled");
            productRepository.save(product1);

            // Product 2
            Product product2 = new Product();
            product2.setName("Logitech MX Master 4 Mouse");
            product2.setDescription("Ergonomic wireless mouse with advanced precision tracking");
            product2.setPrice(129.99);
            product2.setStockQuantity(100);
            product2.setSku("MOU-002");
            product2.setSpecifications("Battery: 70 days, Bluetooth/USB, 4000 DPI sensor");
            productRepository.save(product2);

            // Product 3
            Product product3 = new Product();
            product3.setName("Dell UltraSharp 27 Monitor");
            product3.setDescription("27-inch 4K UHD monitor with IPS technology");
            product3.setPrice(499.99);
            product3.setStockQuantity(30);
            product3.setSku("MON-003");
            product3.setSpecifications("Resolution: 3840x2160, Refresh Rate: 60Hz, Ports: HDMI/DP/USB-C");
            productRepository.save(product3);

            // Product 4
            Product product4 = new Product();
            product4.setName("Kingston NV2 SSD 1TB");
            product4.setDescription("High-speed NVMe SSD for faster boot and load times");
            product4.setPrice(89.99);
            product4.setStockQuantity(200);
            product4.setSku("SSD-004");
            product4.setSpecifications("Interface: PCIe 4.0, Read: 3500MB/s, Write: 2100MB/s");
            productRepository.save(product4);

            // Product 5
            Product product5 = new Product();
            product5.setName("Canon EOS R10 Camera");
            product5.setDescription("Mirrorless camera with 24.2MP sensor and 4K video recording");
            product5.setPrice(999.99);
            product5.setStockQuantity(15);
            product5.setSku("CAM-005");
            product5.setSpecifications("Sensor: APS-C, Lens Mount: RF, Connectivity: Wi-Fi/Bluetooth");
            productRepository.save(product5);

            // Create order
            // Order 1
            Order order1 = new Order();
            order1.setOrderNumber("ORD-2026-001");
            order1.setCustomer(customer1);
            order1.setProduct(product1);
            order1.setQuantity(1);
            order1.setTotalPrice(199.99);
            order1.setStatus(OrderStatus.SHIPPED);
            order1.setShippingAddress(customer1.getAddress()); // use customer address
            order1.setShippedAt(LocalDateTime.now().minusDays(2));
            orderRepository.save(order1);

            // Order 2
            Order order2 = new Order();
            order2.setOrderNumber("ORD-2026-002");
            order2.setCustomer(customer2);
            order2.setProduct(product2);
            order2.setQuantity(2);
            order2.setTotalPrice(259.98);
            order2.setStatus(OrderStatus.PROCESSING);
            order2.setShippingAddress(customer2.getAddress()); // use customer address
            order2.setShippedAt(null);
            orderRepository.save(order2);

            // Order 3
            Order order3 = new Order();
            order3.setOrderNumber("ORD-2026-003");
            order3.setCustomer(customer3);
            order3.setProduct(product3);
            order3.setQuantity(1);
            order3.setTotalPrice(499.99);
            order3.setStatus(OrderStatus.DELIVERED);
            order3.setShippingAddress(customer3.getAddress()); // use customer address
            order3.setShippedAt(LocalDateTime.now().minusDays(5));
            orderRepository.save(order3);

            // Order 4
            Order order4 = new Order();
            order4.setOrderNumber("ORD-2026-004");
            order4.setCustomer(customer4);
            order4.setProduct(product4);
            order4.setQuantity(3);
            order4.setTotalPrice(269.97);
            order4.setStatus(OrderStatus.SHIPPED);
            order4.setShippingAddress(customer4.getAddress()); // use customer address
            order4.setShippedAt(LocalDateTime.now().minusDays(1));
            orderRepository.save(order4);

            // Order 5
            Order order5 = new Order();
            order5.setOrderNumber("ORD-2026-005");
            order5.setCustomer(customer5);
            order5.setProduct(product5);
            order5.setQuantity(1);
            order5.setTotalPrice(999.99);
            order5.setStatus(OrderStatus.CANCELLED);
            order5.setShippingAddress(customer5.getAddress()); // use customer address
            order5.setShippedAt(null);
            orderRepository.save(order5);

            log.info("Sample data initialized");
        }
    }
}
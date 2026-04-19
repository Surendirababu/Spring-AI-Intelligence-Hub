package com.intelligencehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class IntelligenceHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligenceHubApplication.class, args);
    }

    @GetMapping("/health")
    public String health() {
        return "Intelligence Hub is running!";
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Intelligence Hub - Technical Support Co-Pilot\n" +
               "API Endpoints:\n" +
               "POST /api/chat - Send a query\n" +
               "POST /api/documents/upload - Upload product manual\n" +
               "GET /api/chat/health - Health check\n" +
               "GET /api/documents/health - Document service health";
    }
}

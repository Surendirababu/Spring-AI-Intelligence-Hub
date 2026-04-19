package com.intelligencehub.controller;

import com.intelligencehub.dto.ChatRequest;
import com.intelligencehub.dto.ChatResponse;
import com.intelligencehub.service.agent.IntelligenceAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * ChatController - API for processing customer queries
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final IntelligenceAgentService intelligenceAgentService;

    @Autowired
    public ChatController(IntelligenceAgentService intelligenceAgentService) {
        this.intelligenceAgentService = intelligenceAgentService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: {}", request.query());

            if (request.query() == null || request.query().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(ChatResponse.error("Query cannot be empty"));
            }

            String response = intelligenceAgentService.processQuery(request.query());

            ChatResponse chatResponse = ChatResponse.success(response, LocalDateTime.now());

            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ChatResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat API is running");
    }
}

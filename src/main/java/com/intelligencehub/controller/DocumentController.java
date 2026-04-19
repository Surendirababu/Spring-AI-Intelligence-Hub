package com.intelligencehub.controller;

import com.intelligencehub.service.document.DocumentProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DocumentController - API for uploading and processing documents
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;

    @Autowired
    public DocumentController(DocumentProcessingService documentProcessingService) {
        this.documentProcessingService = documentProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProductManual(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") String productId) {

        try {
            log.info("Uploading document for product: {}", productId);

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "File is empty"));
            }

            if (!documentProcessingService.isSupportedFileType(
                    file.getContentType(), file.getOriginalFilename())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, 
                        "error", "Unsupported file type"));
            }

            int chunksCreated = documentProcessingService
                .processProductManual(file, productId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Document processed successfully",
                "filename", file.getOriginalFilename(),
                "chunksCreated", chunksCreated,
                "productId", productId,
                "timestamp", LocalDateTime.now()
            ));

        } catch (IOException e) {
            log.error("Error processing document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Document Service is running");
    }
}

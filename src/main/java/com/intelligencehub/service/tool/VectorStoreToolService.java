package com.intelligencehub.service.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * VectorStoreToolService - Tools for querying unstructured data from PDFs
 */
@Slf4j
@Service
public class VectorStoreToolService {

    private final VectorStore vectorStore;

    @Autowired
    public VectorStoreToolService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String searchProductManual(String query, int topK) {
        log.info("Tool called: searchProductManual(query={}, topK={})", query, topK);

        List<Document> results = vectorStore.similaritySearch(query).stream()
            .limit(topK)
            .toList();

        if (results.isEmpty()) {
            return "No relevant documentation found for query: " + query;
        }

        StringBuilder response = new StringBuilder();
        response.append("Found ").append(results.size()).append(" relevant sections:\n\n");

        for (int i = 0; i < results.size(); i++) {
            Document doc = results.get(i);
            response.append(String.format(
                "--- Section %d (From: %s) ---\n%s\n\n",
                i + 1,
                doc.getMetadata().get("filename"),
                doc.getText()
            ));
        }

        return response.toString();
    }

    public String getTroubleshootingGuide(String productId, String issue) {
        log.info("Tool called: getTroubleshootingGuide(productId={}, issue={})", 
                 productId, issue);

        String searchQuery = String.format("troubleshoot %s issue %s", productId, issue);

        List<Document> results = vectorStore.similaritySearch(searchQuery)
            .stream()
            .filter(doc -> {
                Object pId = doc.getMetadata().get("productId");
                return pId != null && pId.toString().equals(productId);
            })
            .limit(5)
            .toList();

        if (results.isEmpty()) {
            return "No troubleshooting guide found for product: " + productId + 
                   " issue: " + issue;
        }

        StringBuilder guide = new StringBuilder();
        guide.append("Troubleshooting Guide:\n\n");

        for (Document doc : results) {
            guide.append(doc.getText()).append("\n\n");
        }

        return guide.toString();
    }
}

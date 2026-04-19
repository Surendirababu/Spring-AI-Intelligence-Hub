package com.intelligencehub.service.agent;

import com.intelligencehub.service.tool.DatabaseToolService;
import com.intelligencehub.service.tool.VectorStoreToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class IntelligenceAgentService {

    private final ChatClient chatClient;
    private final DatabaseToolService databaseToolService;
    private final VectorStoreToolService vectorStoreToolService;
    private final String systemPrompt;

    @Autowired
    public IntelligenceAgentService(
            ChatClient.Builder chatClientBuilder,
            DatabaseToolService databaseToolService,
            VectorStoreToolService vectorStoreToolService,
            @Value("classpath:/prompts/agent-system-prompt.txt") Resource promptResource)
            throws IOException {

        this.chatClient = chatClientBuilder.build();
        this.databaseToolService = databaseToolService;
        this.vectorStoreToolService = vectorStoreToolService;
        this.systemPrompt = promptResource.getContentAsString(StandardCharsets.UTF_8);

        log.info("IntelligenceAgentService initialized");
    }

    /**
     * Main agent method - processes user query
     */
    public String processQuery(String userQuery) {
        log.info("Processing query: {}", userQuery);

        String toolDecision = analyzeQueryTools(userQuery);
        log.debug("Tool decision: {}", toolDecision);

        Map<String, String> toolResults = executeTools(toolDecision, userQuery);
        String finalResponse = generateResponse(userQuery, toolResults);

        log.info("Query processed successfully");
        return finalResponse;
    }

    /**
     * Analyze query to determine which tools to use
     */
    private String analyzeQueryTools(String userQuery) {
        String analysisPrompt = String.format(
                "Analyze this customer support query and determine which tools to use:\n" +
                        "Query: \"%s\"\n\n" +
                        "Available tools:\n" +
                        "1. findOrderStatus - Look up order status\n" +
                        "2. getProductSpecifications - Get product details\n" +
                        "3. searchProductManual - Search product documentation\n" +
                        "4. getTroubleshootingGuide - Get troubleshooting steps\n" +
                        "Respond with tool names you need, comma separated.",
                userQuery);

        return chatClient.prompt()
                .user(analysisPrompt)
                .call()
                .content();
    }

    /**
     * Execute identified tools
     */
    private Map<String, String> executeTools(String toolDecision, String userQuery) {
        Map<String, String> results = new HashMap<>();

        log.debug("Executing tools: {}", toolDecision);

        if (toolDecision.toLowerCase().contains("findorderstatus")) {
            String orderNumber = extractOrderNumber(userQuery);
            log.debug("Order Number: {}", orderNumber);
            if (!orderNumber.isEmpty()) {
                String result = databaseToolService.findOrderStatus(orderNumber).toString();
                results.put("orderStatus", result);
            }
        }

        if (toolDecision.toLowerCase().contains("getproductspecifications")) {
            String productId = extractProductId(userQuery);
            if (!productId.isEmpty()) {
                String result = databaseToolService.getProductSpecifications(productId);
                results.put("productSpecs", result);
            }
        }

        if (toolDecision.toLowerCase().contains("searchproductmanual")) {
            String result = vectorStoreToolService.searchProductManual(userQuery, 3);
            results.put("productManual", result);
        }

        if (toolDecision.toLowerCase().contains("gettroubleshootingguide")) {
            String productId = extractProductId(userQuery);
            String issue = extractIssue(userQuery);
            String result = vectorStoreToolService.getTroubleshootingGuide(productId, issue);
            results.put("troubleshootingGuide", result);
        }

        return results;
    }

    /**
     * Generate final response using LLM
     */
    private String generateResponse(String userQuery, Map<String, String> toolResults) {
        StringBuilder context = new StringBuilder();

        if (!toolResults.isEmpty()) {
            context.append("Context from tools:\n");
            for (Map.Entry<String, String> entry : toolResults.entrySet()) {
                context.append("\n--- ").append(entry.getKey()).append(" ---\n");
                context.append(entry.getValue()).append("\n");
            }
        }

        String responsePrompt = String.format(
                "%s\n\nCustomer Query: \"%s\"\n\n%s\n\n" +
                        "Provide a helpful, concise response that addresses the customer's query.",
                systemPrompt,
                userQuery,
                context.toString());

        return chatClient.prompt()
                .user(responsePrompt)
                .call()
                .content();
    }

    private String extractOrderNumber(String query) {
        Pattern pattern = Pattern.compile("(ORD-[A-Z0-9]+(?:-[A-Z0-9]+)*)");
        Matcher matcher = pattern.matcher(query.toUpperCase());
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractProductId(String query) {
        Pattern pattern = Pattern.compile("(?:product|item|model)\\s*(?:id|number|#)?\\s*(\\d+)");
        Matcher matcher = pattern.matcher(query.toLowerCase());
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractIssue(String query) {
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("not working"))
            return "not working";
        if (lowerQuery.contains("broken"))
            return "broken";
        if (lowerQuery.contains("crash"))
            return "crash";
        if (lowerQuery.contains("slow"))
            return "slow";
        if (lowerQuery.contains("error"))
            return "error";
        return "issue";
    }

}

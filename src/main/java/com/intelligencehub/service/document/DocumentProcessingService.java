package com.intelligencehub.service.document;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class DocumentProcessingService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    @Autowired
    public DocumentProcessingService(
            VectorStore vectorStore,
            TokenTextSplitter tokenTextSplitter) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = tokenTextSplitter;
    }

    /**
     * Process a PDF/Document file and store embeddings in vector database
     */
    public int processProductManual(MultipartFile file, String productId) throws IOException {
        log.info("Processing document: {} for product: {}", 
                 file.getOriginalFilename(), productId);

        List<Document> documents = loadDocument(file);
        enrichMetadata(documents, file, productId);
        List<Document> chunks = chunkDocuments(documents);
        storeDocuments(chunks);

        log.info("Successfully processed {} chunks from {}", 
                 chunks.size(), file.getOriginalFilename());

        return chunks.size();
    }

    private List<Document> loadDocument(MultipartFile file) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if ("application/pdf".equals(contentType) || 
            (filename != null && filename.toLowerCase().endsWith(".pdf"))) {
            log.debug("Using PagePdfDocumentReader for: {}", filename);
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
            return pdfReader.get();
        }

        log.debug("Using TikaDocumentReader for: {}", filename);
        TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
        return tikaReader.get();
    }

    private void enrichMetadata(List<Document> documents, MultipartFile file, String productId) {
        String parentDocId = UUID.randomUUID().toString();

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            Map<String, Object> metadata = new HashMap<>(doc.getMetadata());

            metadata.put("filename", file.getOriginalFilename());
            metadata.put("productId", productId);
            metadata.put("uploadedAt", LocalDateTime.now().toString());
            metadata.put("pageNumber", i + 1);
            metadata.put("sourceType", "PRODUCT_MANUAL");
            metadata.put("parentDocId", parentDocId);

            String text = doc.getText() != null ? doc.getText() : "";
            documents.set(i, new Document(text, metadata));
        }
    }

    private List<Document> chunkDocuments(List<Document> documents) {
        log.debug("Chunking {} documents", documents.size());
        List<Document> chunks = new ArrayList<>(tokenTextSplitter.apply(documents));

        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());

            metadata.put("chunkIndex", i);
            metadata.put("totalChunks", chunks.size());
            metadata.put("chunkType", "TEXT_CHUNK");

            String text = chunk.getText() != null ? chunk.getText() : "";
            chunks.set(i, new Document(text, metadata));
        }

        return chunks;
    }

    private void storeDocuments(List<Document> documents) {
        vectorStore.add(documents);
        log.info("Stored {} documents in vector store", documents.size());
    }

    public boolean isSupportedFileType(String contentType, String filename) {
        if (contentType != null) {
            if (contentType.equals("application/pdf") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("application/msword") ||
                contentType.equals("text/plain")) {
                return true;
            }
        }

        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            return lowerFilename.endsWith(".pdf") ||
                   lowerFilename.endsWith(".docx") ||
                   lowerFilename.endsWith(".doc") ||
                   lowerFilename.endsWith(".txt");
        }

        return false;
    }
}

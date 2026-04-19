package com.intelligencehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// TroubleshootingGuide Entity
@Entity
@Table(name = "troubleshooting_guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TroubleshootingGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String embeddings;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}

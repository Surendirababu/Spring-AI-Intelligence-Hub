package com.intelligencehub.repository;

import com.intelligencehub.entity.TroubleshootingGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TroubleshootingGuideRepository extends JpaRepository<TroubleshootingGuide, Long> {
    List<TroubleshootingGuide> findByProductId(String productId);
}

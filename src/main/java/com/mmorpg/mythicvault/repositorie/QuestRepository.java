package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Quest;

@Repository
public interface QuestRepository
        extends JpaRepository<Quest, Long>, JpaSpecificationExecutor<Quest> {

    // Spec-based listing (DTO currently doesn't touch steps/rewards, so graph is minimal)
    @Override
    @EntityGraph(attributePaths = {})  // empty for now; safe default
    Page<Quest> findAll(@Nullable Specification<Quest> spec, Pageable pageable);

    // Convenience finders you already use/mentioned
    List<Quest> findByMinLevelLessThanEqual(int level);

    @EntityGraph(attributePaths = {"steps","rewards"})
    List<Quest> findByRepeatableTrue();

    // For a detailed single load with steps & rewards (if you later expand QuestDto)
    @EntityGraph(attributePaths = {"steps","rewards"})
    @Query("select q from Quest q where q.id = :id")
    java.util.Optional<Quest> findDetailedById(@org.springframework.data.repository.query.Param("id") Long id);
}

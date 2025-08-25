package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.LeaderboardEntry;
import com.mmorpg.mythicvault.entity.LeaderboardEntryId;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, LeaderboardEntryId> {

	@EntityGraph(attributePaths = { "character" })
	List<LeaderboardEntry> findBySnapshotIdOrderByIdRankAsc(Long snapshotId);

	@EntityGraph(attributePaths = { "character" })
	Page<LeaderboardEntry> findByIdSnapshotId(Long snapshotId, Pageable pageable);
}
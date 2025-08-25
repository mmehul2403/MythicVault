package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.LeaderboardSnapshot;

@Repository
public interface LeaderboardSnapshotRepository extends JpaRepository<LeaderboardSnapshot, Long> {
	@Query("""
			select s from LeaderboardSnapshot s
			join s.leaderboardType lt
			where lt.code = :type
			order by s.capturedAt desc
			""")
	List<LeaderboardSnapshot> findRecentByType(@Param("type") String type);
}
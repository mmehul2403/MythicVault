package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.LeaderboardType;

@Repository
public interface LeaderboardTypeRepository extends JpaRepository<LeaderboardType, Short> {
	Optional<LeaderboardType> findByCode(String code);
}
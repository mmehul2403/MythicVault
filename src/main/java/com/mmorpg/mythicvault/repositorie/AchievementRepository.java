package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Achievement;


@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
	Optional<Achievement> findByCode(String code);
}
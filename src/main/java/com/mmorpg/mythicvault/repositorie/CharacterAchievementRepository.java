package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterAchievement;
import com.mmorpg.mythicvault.entity.CharacterAchievementId;

@Repository
public interface CharacterAchievementRepository extends JpaRepository<CharacterAchievement, CharacterAchievementId> {
	List<CharacterAchievement> findByCharacterId(Long characterId);
}
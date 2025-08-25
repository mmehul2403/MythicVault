package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterQuestStepProgress;
import com.mmorpg.mythicvault.entity.CharacterQuestStepProgressId;

@Repository
public interface CharacterQuestStepProgressRepository
		extends JpaRepository<CharacterQuestStepProgress, CharacterQuestStepProgressId> {
	List<CharacterQuestStepProgress> findByCharacterId(Long characterId);

	@Query("""
			select p from CharacterQuestStepProgress p
			join p.questStep qs
			where p.character.id = :charId and qs.quest.id = :questId
			""")
	List<CharacterQuestStepProgress> findForQuest(@Param("charId") Long characterId, @Param("questId") Long questId);
}

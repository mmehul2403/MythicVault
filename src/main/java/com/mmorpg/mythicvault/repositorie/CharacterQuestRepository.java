package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterQuest;
import com.mmorpg.mythicvault.entity.CharacterQuestId;

@Repository
public interface CharacterQuestRepository extends JpaRepository<CharacterQuest, CharacterQuestId> {

	@Query("""
			select cq from CharacterQuest cq
			join fetch cq.quest q
			where cq.character.id = :charId
			""")
	List<CharacterQuest> fetchAllForCharacter(@Param("charId") Long characterId);
}
package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterInventory;
import com.mmorpg.mythicvault.entity.CharacterInventoryId;

@Repository
public interface CharacterInventoryRepository extends JpaRepository<CharacterInventory, CharacterInventoryId> {
	List<CharacterInventory> findByCharacterId(Long characterId);

	@Query("""
			select ci from CharacterInventory ci
			join fetch ci.item it
			where ci.character.id = :charId and ci.quantity > 0
			""")
	List<CharacterInventory> fetchActiveInventory(@Param("charId") Long characterId);
}
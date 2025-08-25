package com.mmorpg.mythicvault.repositorie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterEquipment;
import com.mmorpg.mythicvault.entity.CharacterEquipmentId;

@Repository
public interface CharacterEquipmentRepository extends JpaRepository<CharacterEquipment, CharacterEquipmentId> {

	@EntityGraph(attributePaths = { "equipmentSlot", "itemInstance", "itemInstance.item" })
	List<CharacterEquipment> findByCharacterId(Long characterId);

	Optional<CharacterEquipment> findByCharacterIdAndEquipmentSlotCode(Long characterId, String slotCode);
}
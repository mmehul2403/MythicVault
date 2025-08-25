package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterEquipmentId implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "character_id")
	private Long characterId;
	@Column(name = "equipment_slot_id")
	private Short equipmentSlotId;

	public CharacterEquipmentId() {
	}

	public CharacterEquipmentId(Long characterId, Short equipmentSlotId) {
		this.characterId = characterId;
		this.equipmentSlotId = equipmentSlotId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CharacterEquipmentId))
			return false;
		CharacterEquipmentId other = (CharacterEquipmentId) o;
		return Objects.equals(characterId, other.characterId) && Objects.equals(equipmentSlotId, other.equipmentSlotId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterId, equipmentSlotId);
	}
}

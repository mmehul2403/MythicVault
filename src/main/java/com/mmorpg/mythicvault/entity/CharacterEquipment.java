package com.mmorpg.mythicvault.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Entity
@Table(name = "character_equipment")
public class CharacterEquipment {

	@EmbeddedId
	private CharacterEquipmentId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("characterId")
	@JoinColumn(name = "character_id")
	private GameCharacter character;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("equipmentSlotId")
	@JoinColumn(name = "equipment_slot_id")
	private EquipmentSlot equipmentSlot;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_instance_id", unique = true)
	private ItemInstance itemInstance; 

}

package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterInventoryId implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "character_id")
	private Long characterId;
	@Column(name = "item_id")
	private Long itemId;

	public CharacterInventoryId() {
	}

	public CharacterInventoryId(Long characterId, Long itemId) {
		this.characterId = characterId;
		this.itemId = itemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CharacterInventoryId))
			return false;
		CharacterInventoryId other = (CharacterInventoryId) o;
		return Objects.equals(characterId, other.characterId) && Objects.equals(itemId, other.itemId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterId, itemId);
	}
}

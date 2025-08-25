package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ItemStatId {

	@Column(name = "item_id")
	private Long itemId;
	@Column(name = "stat_type_id")
	private Short statTypeId;

	public ItemStatId() {
	}

	public ItemStatId(Long itemId, Short statTypeId) {
		this.itemId = itemId;
		this.statTypeId = statTypeId;
	}

	// equals/hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ItemStatId))
			return false;
		ItemStatId other = (ItemStatId) o;
		return Objects.equals(itemId, other.itemId) && Objects.equals(statTypeId, other.statTypeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, statTypeId);
	}
}

package com.mmorpg.mythicvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_stat")
public class ItemStat {

	@EmbeddedId
	private ItemStatId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("itemId")
	@JoinColumn(name = "item_id")
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("statTypeId")
	@JoinColumn(name = "stat_type_id")
	private StatType statType;

	@Column(name = "value", nullable = false)
	private Double value;

	public ItemStat() {
	}

	public ItemStat(ItemStatId id, Item item, StatType statType, Double value) {
		this.id = id;
		this.item = item;
		this.statType = statType;
		this.value = value;
	}

}

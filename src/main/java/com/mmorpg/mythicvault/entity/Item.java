package com.mmorpg.mythicvault.entity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "text")
	private String description;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "item_type_id", nullable = false)
	private ItemType itemType;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "rarity_id", nullable = false)
	private Rarity rarity;

	@Column(name = "required_level", nullable = false)
	private Integer requiredLevel = 1;

	@Column(name = "max_stack", nullable = false)
	private Integer maxStack = 1;

	@Column(name = "base_value", nullable = false)
	private Long baseValue = 0L;

	@Column(name = "bind_on_pickup", nullable = false)
	private boolean bindOnPickup = false;

	@Column(nullable = false)
	private boolean tradable = true;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ItemStat> stats = new HashSet<>();

	public void addStat(StatType statType, Double value) {
		ItemStat is = new ItemStat(new ItemStatId(this.id, statType.getId()), this, statType, value);
		stats.add(is);
	}
}

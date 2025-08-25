package com.mmorpg.mythicvault.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quest")
public class Quest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "text")
	private String summary;

	@Column(name = "min_level", nullable = false)
	private Integer minLevel = 1;

	@Column(nullable = false)
	private boolean repeatable = false;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<QuestStep> steps = new ArrayList<>();

	@OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<QuestReward> rewards = new HashSet<>();

}

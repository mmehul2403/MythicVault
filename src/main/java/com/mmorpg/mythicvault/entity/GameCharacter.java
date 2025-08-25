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
@Table(name = "\"character\"")
public class GameCharacter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private AccountUser account;

	@Column(nullable = false)
	private String name;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id", nullable = false)
	private CharacterClass characterClass;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "race_id", nullable = false)
	private Race race;

	@Column(nullable = false)
	private Integer level = 1;

	@Column(nullable = false)
	private Long experience = 0L;

	@Column(nullable = false)
	private Long gold = 0L;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	// Relationships
	@OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CharacterInventory> inventory = new HashSet<>();

	@OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CharacterEquipment> equipment = new HashSet<>();

	@OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CharacterQuest> quests = new HashSet<>();

	@OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CharacterAchievement> achievements = new HashSet<>();

}

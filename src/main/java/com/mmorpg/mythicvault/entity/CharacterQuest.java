package com.mmorpg.mythicvault.entity;

import java.time.OffsetDateTime;

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
@Table(name = "character_quest")
public class CharacterQuest {

	@EmbeddedId
	private CharacterQuestId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("characterId")
	@JoinColumn(name = "character_id")
	private GameCharacter character;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("questId")
	@JoinColumn(name = "quest_id")
	private Quest quest;

	@Column(name = "accepted_at", nullable = false)
	private OffsetDateTime acceptedAt;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;
}

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
@Table(name = "character_achievement")
public class CharacterAchievement {

	@EmbeddedId
	private CharacterAchievementId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("characterId")
	@JoinColumn(name = "character_id")
	private GameCharacter character;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("achievementId")
	@JoinColumn(name = "achievement_id")
	private Achievement achievement;

	@Column(name = "unlocked_at", nullable = false)
	private OffsetDateTime unlockedAt;
}

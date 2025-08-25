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
@Table(name = "character_quest_step_progress")
public class CharacterQuestStepProgress {

	@EmbeddedId
	private CharacterQuestStepProgressId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("characterId")
	@JoinColumn(name = "character_id")
	private GameCharacter character;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("questStepId")
	@JoinColumn(name = "quest_step_id")
	private QuestStep questStep;

	@Column(name = "progress_count", nullable = false)
	private Integer progressCount = 0;
}

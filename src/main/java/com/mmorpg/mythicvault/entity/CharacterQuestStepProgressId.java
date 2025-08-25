package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterQuestStepProgressId implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "character_id")
	private Long characterId;
	@Column(name = "quest_step_id")
	private Long questStepId;

	public CharacterQuestStepProgressId() {
	}

	public CharacterQuestStepProgressId(Long characterId, Long questStepId) {
		this.characterId = characterId;
		this.questStepId = questStepId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CharacterQuestStepProgressId))
			return false;
		CharacterQuestStepProgressId other = (CharacterQuestStepProgressId) o;
		return Objects.equals(characterId, other.characterId) && Objects.equals(questStepId, other.questStepId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterId, questStepId);
	}
}

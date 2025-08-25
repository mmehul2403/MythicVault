package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterQuestId {

	@Column(name = "character_id")
	private Long characterId;
	@Column(name = "quest_id")
	private Long questId;

	public CharacterQuestId() {
	}

	public CharacterQuestId(Long characterId, Long questId) {
		this.characterId = characterId;
		this.questId = questId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CharacterQuestId))
			return false;
		CharacterQuestId other = (CharacterQuestId) o;
		return Objects.equals(characterId, other.characterId) && Objects.equals(questId, other.questId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterId, questId);
	}

}

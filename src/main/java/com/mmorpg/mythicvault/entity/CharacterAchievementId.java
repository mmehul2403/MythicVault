package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterAchievementId implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "character_id")
	private Long characterId;
	@Column(name = "achievement_id")
	private Long achievementId;

	public CharacterAchievementId() {
	}

	public CharacterAchievementId(Long characterId, Long achievementId) {
		this.characterId = characterId;
		this.achievementId = achievementId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CharacterAchievementId))
			return false;
		CharacterAchievementId other = (CharacterAchievementId) o;
		return Objects.equals(characterId, other.characterId) && Objects.equals(achievementId, other.achievementId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterId, achievementId);
	}
}

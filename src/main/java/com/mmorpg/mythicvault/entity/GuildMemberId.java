package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GuildMemberId implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "guild_id")
	private Long guildId;
	@Column(name = "character_id")
	private Long characterId;

	public GuildMemberId() {
	}

	public GuildMemberId(Long guildId, Long characterId) {
		this.guildId = guildId;
		this.characterId = characterId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof GuildMemberId))
			return false;
		GuildMemberId other = (GuildMemberId) o;
		return Objects.equals(guildId, other.guildId) && Objects.equals(characterId, other.characterId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(guildId, characterId);
	}
}

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
@Table(name = "guild_member")
public class GuildMember {
	@EmbeddedId
	private GuildMemberId id;


	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("guildId")
	@JoinColumn(name = "guild_id")
	private Guild guild;


	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("characterId")
	@JoinColumn(name = "character_id")
	private GameCharacter character;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private GuildRole role;


	@Column(name = "joined_at", nullable = false)
	private OffsetDateTime joinedAt;
}

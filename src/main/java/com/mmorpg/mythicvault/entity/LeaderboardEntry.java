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
@Table(name = "leaderboard_entry")
public class LeaderboardEntry {
	@EmbeddedId
	private LeaderboardEntryId id;

	@MapsId("snapshotId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "snapshot_id", nullable = false)
	private LeaderboardSnapshot snapshot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "character_id")
	private GameCharacter character;

	@Column(name = "score_value", nullable = false)
	private Integer scoreValue;

	public LeaderboardEntryId getId() {
		return id;
	}

	public Integer getScoreValue() {
		return scoreValue;
	}

	public GameCharacter getCharacter() {
		return character;
	}
}

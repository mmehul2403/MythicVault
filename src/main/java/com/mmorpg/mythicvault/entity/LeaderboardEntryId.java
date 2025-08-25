package com.mmorpg.mythicvault.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
public class LeaderboardEntryId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Column(name = "snapshot_id")
	private Long snapshotId;

	@Getter
	@Setter
	@Column(name = "rank")
	private Integer rank;

	public LeaderboardEntryId() {
	}

	public LeaderboardEntryId(Long snapshotId, Integer rank) {
		this.snapshotId = snapshotId;
		this.rank = rank;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LeaderboardEntryId))
			return false;
		LeaderboardEntryId other = (LeaderboardEntryId) o;
		return Objects.equals(snapshotId, other.snapshotId) && Objects.equals(rank, other.rank);
	}

	@Override
	public int hashCode() {
		return Objects.hash(snapshotId, rank);
	}
}

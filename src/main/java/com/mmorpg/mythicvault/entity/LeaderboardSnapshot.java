package com.mmorpg.mythicvault.entity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "leaderboard_snapshot")
public class LeaderboardSnapshot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "leaderboard_type_id", nullable = false)
	private LeaderboardType leaderboardType;

	@Column(name = "captured_at", nullable = false)
	private OffsetDateTime capturedAt;

	@OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<LeaderboardEntry> entries = new HashSet<>();
}

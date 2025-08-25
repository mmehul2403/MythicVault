package com.mmorpg.mythicvault.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ the correct Pageable

import com.mmorpg.mythicvault.dto.LeaderboardEntryDto;
import com.mmorpg.mythicvault.dto.LeaderboardLatestResponse;
import com.mmorpg.mythicvault.dto.LeaderboardSnapshotDto;

public interface LeaderboardService {
	LeaderboardLatestResponse latestByType(String typeCode, Pageable pageable);

	Page<LeaderboardSnapshotDto> listSnapshots(String typeCode, Pageable pageable);

	Page<LeaderboardEntryDto> listEntries(Long snapshotId, Pageable pageable);

	LeaderboardSnapshotDto getSnapshot(Long snapshotId);
}

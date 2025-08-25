package com.mmorpg.mythicvault.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mmorpg.mythicvault.dto.LeaderboardEntryDto;
import com.mmorpg.mythicvault.dto.LeaderboardLatestResponse;
import com.mmorpg.mythicvault.dto.LeaderboardSnapshotDto;
import com.mmorpg.mythicvault.service.LeaderboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Leaderboards")
@RestController
@RequestMapping("/api/leaderboards")
public class LeaderboardController {

	private final LeaderboardService service;

	public LeaderboardController(LeaderboardService service) {
		this.service = service;
	}

	@Operation(summary = "Get latest snapshot and top entries by leaderboard type")
	@GetMapping("/{typeCode}/latest")
	public LeaderboardLatestResponse latestByType(@PathVariable String typeCode,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return service.latestByType(typeCode, pageable);
	}

	@Operation(summary = "List snapshots for a leaderboard type (newest first)")
	@GetMapping("/{typeCode}/snapshots")
	public Page<LeaderboardSnapshotDto> listSnapshots(@PathVariable String typeCode,
			@PageableDefault(size = 20, sort = "capturedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return service.listSnapshots(typeCode, pageable);
	}

	@Operation(summary = "List entries for a snapshot")
	@GetMapping("/snapshots/{snapshotId}/entries")
	public Page<LeaderboardEntryDto> listEntries(@PathVariable Long snapshotId,
			@PageableDefault(size = 50) Pageable pageable) {
		return service.listEntries(snapshotId, pageable);
	}

	@Operation(summary = "Get snapshot summary")
	@GetMapping("/snapshots/{snapshotId}")
	public LeaderboardSnapshotDto getSnapshot(@PathVariable Long snapshotId) {
		return service.getSnapshot(snapshotId);
	}

}

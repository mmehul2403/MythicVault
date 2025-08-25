package com.mmorpg.mythicvault.service;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmorpg.mythicvault.dto.LeaderboardEntryDto;
import com.mmorpg.mythicvault.dto.LeaderboardLatestResponse;
import com.mmorpg.mythicvault.dto.LeaderboardSnapshotDto;
import com.mmorpg.mythicvault.entity.LeaderboardEntry;
import com.mmorpg.mythicvault.entity.LeaderboardSnapshot;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.LeaderboardEntryRepository;
import com.mmorpg.mythicvault.repositorie.LeaderboardSnapshotRepository;
import com.mmorpg.mythicvault.repositorie.LeaderboardTypeRepository;

@Service
@Transactional
public class LeaderboardServiceImpl implements LeaderboardService {

	private final LeaderboardTypeRepository typeRepo;
	private final LeaderboardSnapshotRepository snapshotRepo;
	private final LeaderboardEntryRepository entryRepo;

	public LeaderboardServiceImpl(LeaderboardTypeRepository typeRepo, LeaderboardSnapshotRepository snapshotRepo,
			LeaderboardEntryRepository entryRepo) {
		this.typeRepo = typeRepo;
		this.snapshotRepo = snapshotRepo;
		this.entryRepo = entryRepo;
	}

	@Override
	@Transactional(readOnly = true)
	public LeaderboardLatestResponse latestByType(String typeCode, Pageable pageable) {
		var type = typeRepo.findByCode(typeCode)
				.orElseThrow(() -> new ResourceNotFoundException("Unknown leaderboard type: " + typeCode));

		var snapshots = snapshotRepo.findRecentByType(type.getCode());
		if (snapshots == null || snapshots.isEmpty()) {
			throw new ResourceNotFoundException("No snapshots found for type: " + typeCode);
		}
		var latest = snapshots.get(0);

		// top entries using pageable size (page index ignored; treat it as "top N")
		var topPageable = PageRequest.of(0, pageable.getPageSize());
		var page = entryRepo.findByIdSnapshotId(latest.getId(), topPageable);

		var dto = new LeaderboardLatestResponse(toSnapshotDto(latest),
				page.getContent().stream().map(LeaderboardServiceImpl::toEntryDto).collect(Collectors.toList()));
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LeaderboardSnapshotDto> listSnapshots(String typeCode, Pageable pageable) {
		// You provided a non-pageable repo method; we’ll convert to Page manually
		var list = snapshotRepo.findRecentByType(typeCode);
		if (list == null || list.isEmpty())
			return Page.empty(pageable);

		// Sort by capturedAt DESC already in query; just slice
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), list.size());
		if (start >= list.size())
			return new PageImpl<>(java.util.List.of(), pageable, list.size());

		var content = list.subList(start, end).stream().map(LeaderboardServiceImpl::toSnapshotDto).toList();
		return new PageImpl<>(content, pageable, list.size());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LeaderboardEntryDto> listEntries(Long snapshotId, Pageable pageable) {
		// pageable repo method
		var page = entryRepo.findByIdSnapshotId(snapshotId, pageable);
		// Ensure snapshot exists (nicer error than empty)
		if (page.getTotalElements() == 0) {
			// Try a quick existence check
			snapshotRepo.findById(snapshotId)
					.orElseThrow(() -> new ResourceNotFoundException("Snapshot not found: " + snapshotId));
		}
		return page.map(LeaderboardServiceImpl::toEntryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public LeaderboardSnapshotDto getSnapshot(Long snapshotId) {
		var s = snapshotRepo.findById(snapshotId)
				.orElseThrow(() -> new ResourceNotFoundException("Snapshot not found: " + snapshotId));
		return toSnapshotDto(s);
	}

	private static LeaderboardSnapshotDto toSnapshotDto(LeaderboardSnapshot s) {
		var code = (s.getLeaderboardType() != null) ? s.getLeaderboardType().getCode() : null;
		return new LeaderboardSnapshotDto(s.getId(), code, s.getCapturedAt());
	}

	private static LeaderboardEntryDto toEntryDto(LeaderboardEntry e) {
		var rank = (e.getId() != null && e.getId().getRank() != null) ? e.getId().getRank() : 0;

		var c = e.getCharacter();
		Long charId = (c != null && c.getId() != null) ? c.getId().longValue() : null;
		String charName = (c != null) ? c.getName() : null;

		return new LeaderboardEntryDto((int) rank, charId, charName, (long) e.getScoreValue());
	}

}

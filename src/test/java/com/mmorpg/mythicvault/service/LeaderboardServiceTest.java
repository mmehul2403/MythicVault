package com.mmorpg.mythicvault.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.mmorpg.mythicvault.dto.LeaderboardEntryDto;
import com.mmorpg.mythicvault.dto.LeaderboardLatestResponse;
import com.mmorpg.mythicvault.dto.LeaderboardSnapshotDto;
import com.mmorpg.mythicvault.entity.GameCharacter;
import com.mmorpg.mythicvault.entity.LeaderboardEntry;
import com.mmorpg.mythicvault.entity.LeaderboardEntryId;
import com.mmorpg.mythicvault.entity.LeaderboardSnapshot;
import com.mmorpg.mythicvault.entity.LeaderboardType;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.LeaderboardEntryRepository;
import com.mmorpg.mythicvault.repositorie.LeaderboardSnapshotRepository;
import com.mmorpg.mythicvault.repositorie.LeaderboardTypeRepository;

class LeaderboardServiceTest {

	private final LeaderboardTypeRepository typeRepo = mock(LeaderboardTypeRepository.class);
	private final LeaderboardSnapshotRepository snapRepo = mock(LeaderboardSnapshotRepository.class);
	private final LeaderboardEntryRepository entryRepo = mock(LeaderboardEntryRepository.class);

	private LeaderboardService svc() {
		return new LeaderboardServiceImpl(typeRepo, snapRepo, entryRepo);
	}

	private static LeaderboardType type(String code) {
		var t = new LeaderboardType();
		t.setCode(code);
		return t;
	}

	private static LeaderboardSnapshot snapshot(long id, LeaderboardType t) {
		var s = new LeaderboardSnapshot();
		s.setId(id);
		s.setLeaderboardType(t);
		s.setCapturedAt(OffsetDateTime.now());
		return s;
	}

	private static GameCharacter character(Long id, String name) {
		var c = new GameCharacter();
		c.setId(id);
		c.setName(name);
		return c;
	}

	private static LeaderboardEntry entry(Long snapId, Integer rank, GameCharacter c, Integer score) {
		var e = new LeaderboardEntry();
		if (snapId != null && rank != null)
			e.setId(new LeaderboardEntryId(snapId, rank));
		e.setCharacter(c);
		e.setScoreValue(score);
		return e;
	}

	@Nested
	class LatestByType {

		@Test
		void happyPath_usesTopN_andMaps() {
			var t = type("weekly");
			when(typeRepo.findByCode("weekly")).thenReturn(Optional.of(t));
			var s = snapshot(42L, t);
			when(snapRepo.findRecentByType("weekly")).thenReturn(List.of(s));

			var gc = character(7L, "Thorin");
			var e1 = entry(42L, 1, gc, 9001);

			when(entryRepo.findByIdSnapshotId(eq(42L), any(PageRequest.class)))
					.thenAnswer(inv -> new PageImpl<>(List.of(e1), inv.getArgument(1), 1));

			LeaderboardLatestResponse res = svc().latestByType("weekly", PageRequest.of(99, 5));
			assertThat(res.snapshot()).isNotNull();
			assertThat(res.snapshot().id()).isEqualTo(42L);
			assertThat(res.topEntries()).hasSize(1);
			var dto = res.topEntries().get(0);
			assertThat(dto.rank()).isEqualTo(1);
			assertThat(dto.characterId()).isEqualTo(7L);
			assertThat(dto.characterName()).isEqualTo("Thorin");
			assertThat(dto.scoreValue()).isEqualTo(9001L);
		}

		@Test
		void unknownType_throws404() {
			when(typeRepo.findByCode("x")).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().latestByType("x", PageRequest.of(0, 5)))
					.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Unknown leaderboard type");
		}

		@Test
		void noSnapshots_emptyList_throws404() {
			var t = type("weekly");
			when(typeRepo.findByCode("weekly")).thenReturn(Optional.of(t));
			when(snapRepo.findRecentByType("weekly")).thenReturn(List.of());
			assertThatThrownBy(() -> svc().latestByType("weekly", PageRequest.of(0, 5)))
					.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("No snapshots found");
		}

		@Test
		void noSnapshots_nullList_throws404() {
			var t = type("weekly");
			when(typeRepo.findByCode("weekly")).thenReturn(Optional.of(t));
			when(snapRepo.findRecentByType("weekly")).thenReturn(null);
			assertThatThrownBy(() -> svc().latestByType("weekly", PageRequest.of(0, 5)))
					.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("No snapshots found");
		}
	}

	@Nested
	class ListSnapshots {

		@Test
		void paginates_slices_and_maps() {
			var t = type("weekly");
			var list = new ArrayList<LeaderboardSnapshot>();
			for (int i = 1; i <= 7; i++)
				list.add(snapshot(i, t));
			when(snapRepo.findRecentByType("weekly")).thenReturn(list);

			var page = svc().listSnapshots("weekly", PageRequest.of(1, 3)); // items 4..6
			assertThat(page.getTotalElements()).isEqualTo(7);
			assertThat(page.getContent()).hasSize(3);
			List<LeaderboardSnapshotDto> c = page.getContent();
			assertThat(c.get(0).id()).isEqualTo(4L);
			assertThat(c.get(1).id()).isEqualTo(5L);
			assertThat(c.get(2).id()).isEqualTo(6L);
		}

		@Test
		void emptyList_returnsEmptyPage() {
			when(snapRepo.findRecentByType("weekly")).thenReturn(List.of());
			var page = svc().listSnapshots("weekly", PageRequest.of(0, 10));
			assertThat(page.getTotalElements()).isZero();
			assertThat(page.getContent()).isEmpty();
		}

		@Test
		void startBeyondSize_returnsEmptyPage_withTotal() {
			var t = type("weekly");
			when(snapRepo.findRecentByType("weekly")).thenReturn(List.of(snapshot(1, t), snapshot(2, t)));
			var page = svc().listSnapshots("weekly", PageRequest.of(5, 10));
			assertThat(page.getTotalElements()).isEqualTo(2);
			assertThat(page.getContent()).isEmpty();
		}
	}

	@Nested
	class ListEntries {

		@Test
		void mapsFields_andNullSafety() {
			var snapId = 55L;
			var pageable = PageRequest.of(0, 10);

			// entry1: null id/character → rank=0, char fields null
			var e1 = new LeaderboardEntry();
			e1.setId(null);
			e1.setCharacter(null);
			e1.setScoreValue(123);

			// entry2: id present, character id null, name set
			var e2 = new LeaderboardEntry();
			e2.setId(new LeaderboardEntryId(snapId, 3));
			var c = character(null, "Nameless");
			e2.setCharacter(c);
			e2.setScoreValue(456);

			when(entryRepo.findByIdSnapshotId(eq(snapId), eq(pageable)))
					.thenReturn(new PageImpl<>(List.of(e1, e2), pageable, 2));

			var page = svc().listEntries(snapId, pageable);
			List<LeaderboardEntryDto> dtos = page.getContent();

			assertThat(dtos.get(0).rank()).isEqualTo(0);
			assertThat(dtos.get(0).characterId()).isNull();
			assertThat(dtos.get(0).characterName()).isNull();
			assertThat(dtos.get(0).scoreValue()).isEqualTo(123L);

			assertThat(dtos.get(1).rank()).isEqualTo(3);
			assertThat(dtos.get(1).characterId()).isNull();
			assertThat(dtos.get(1).characterName()).isEqualTo("Nameless");
			assertThat(dtos.get(1).scoreValue()).isEqualTo(456L);
		}

		@Test
		void empty_butSnapshotExists_returnsEmpty() {
			var snapId = 99L;
			var pageable = PageRequest.of(0, 5);

			when(entryRepo.findByIdSnapshotId(eq(snapId), eq(pageable))).thenReturn(Page.empty(pageable));
			when(snapRepo.findById(snapId)).thenReturn(Optional.of(snapshot(snapId, type("weekly"))));

			var page = svc().listEntries(snapId, pageable);
			assertThat(page.getTotalElements()).isZero();
			assertThat(page.getContent()).isEmpty();
		}

		@Test
		void empty_andSnapshotMissing_throws404() {
			var snapId = 1000L;
			var pageable = PageRequest.of(0, 5);

			when(entryRepo.findByIdSnapshotId(eq(snapId), eq(pageable))).thenReturn(Page.empty(pageable));
			when(snapRepo.findById(snapId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().listEntries(snapId, pageable)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Snapshot not found");
		}
	}

	@Nested
	class GetSnapshot {
		@Test
		void ok() {
			when(snapRepo.findById(7L)).thenReturn(Optional.of(snapshot(7L, type("weekly"))));
			var dto = svc().getSnapshot(7L);
			assertThat(dto.id()).isEqualTo(7L);
			assertThat(dto.typeCode()).isEqualTo("weekly");
			assertThat(dto.capturedAt()).isNotNull();
		}

		@Test
		void notFound() {
			when(snapRepo.findById(404L)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().getSnapshot(404L)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Snapshot not found");
		}
	}
}

package com.mmorpg.mythicvault.dto;

import java.util.List;

public record LeaderboardLatestResponse(LeaderboardSnapshotDto snapshot, List<LeaderboardEntryDto> topEntries) {

}

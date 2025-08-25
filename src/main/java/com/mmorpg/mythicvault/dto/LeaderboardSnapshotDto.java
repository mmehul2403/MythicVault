package com.mmorpg.mythicvault.dto;

import java.time.OffsetDateTime;

public record LeaderboardSnapshotDto(Long id, String typeCode, OffsetDateTime capturedAt) {

}

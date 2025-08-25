package com.mmorpg.mythicvault.dto;

public record LeaderboardEntryDto(int rank, Long characterId, String characterName, Long scoreValue) {

}

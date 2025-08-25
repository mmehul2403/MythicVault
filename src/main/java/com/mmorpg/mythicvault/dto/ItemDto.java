package com.mmorpg.mythicvault.dto;

import java.time.OffsetDateTime;

public record ItemDto(Long id, String name, String description, String itemType, String rarity, Integer requiredLevel,
		Integer maxStack, Long baseValue, boolean bindOnPickup, boolean tradable, OffsetDateTime createdAt,
		OffsetDateTime updatedAt) {

}

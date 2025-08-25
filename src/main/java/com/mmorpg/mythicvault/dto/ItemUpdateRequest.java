package com.mmorpg.mythicvault.dto;

public record ItemUpdateRequest(String name, String description, String itemTypeCode, String rarityCode,
		Integer requiredLevel, Integer maxStack, Long baseValue, Boolean bindOnPickup, Boolean tradable) {

}

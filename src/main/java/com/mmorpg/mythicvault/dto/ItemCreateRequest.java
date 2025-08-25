package com.mmorpg.mythicvault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemCreateRequest(@NotBlank String name, String description, @NotBlank String itemTypeCode,
		@NotBlank String rarityCode, @Min(1) Integer requiredLevel, @Min(1) Integer maxStack, @Min(0) Long baseValue,
		boolean bindOnPickup, boolean tradable) {

}

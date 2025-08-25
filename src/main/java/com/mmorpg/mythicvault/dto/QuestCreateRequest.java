package com.mmorpg.mythicvault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record QuestCreateRequest(@NotBlank String title,
		String summary,
		@Min(1) Integer minLevel,
		Boolean repeatable) {

}

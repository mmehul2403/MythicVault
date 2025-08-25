package com.mmorpg.mythicvault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CharacterCreateRequest(@NotBlank String username, @NotBlank String name, @NotBlank String classCode,
		@NotBlank String raceCode, @Min(1) Integer level) {

}

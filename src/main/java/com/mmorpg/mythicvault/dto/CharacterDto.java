package com.mmorpg.mythicvault.dto;

public record CharacterDto(Long id, String username, String name, String classCode, String raceCode, Integer level,
		Long experience, Long gold) {

}

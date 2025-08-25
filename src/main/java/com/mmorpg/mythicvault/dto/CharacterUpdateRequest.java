package com.mmorpg.mythicvault.dto;

public record CharacterUpdateRequest(String name, String classCode, String raceCode, Integer level, Long experience,
		Long gold) {

}

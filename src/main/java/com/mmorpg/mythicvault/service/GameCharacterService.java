package com.mmorpg.mythicvault.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mmorpg.mythicvault.dto.CharacterCreateRequest;
import com.mmorpg.mythicvault.dto.CharacterDto;
import com.mmorpg.mythicvault.dto.CharacterUpdateRequest;

public interface GameCharacterService {
	Page<CharacterDto> list(String username, String name, String classCode, String raceCode, Integer minLevel,
			Integer maxLevel, Pageable pageable);

	CharacterDto get(Long id);

	CharacterDto create(CharacterCreateRequest req);

	CharacterDto update(Long id, CharacterUpdateRequest req);

	void delete(Long id);
}

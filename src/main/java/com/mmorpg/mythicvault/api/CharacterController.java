package com.mmorpg.mythicvault.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmorpg.mythicvault.dto.CharacterCreateRequest;
import com.mmorpg.mythicvault.dto.CharacterDto;
import com.mmorpg.mythicvault.dto.CharacterUpdateRequest;
import com.mmorpg.mythicvault.service.GameCharacterService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Characters")
@RestController
@RequestMapping("/api/characters")
public class CharacterController {

	private final GameCharacterService characterService;

	public CharacterController(GameCharacterService characterService) {
		this.characterService = characterService;
	}

	@GetMapping
	public Page<CharacterDto> list(@RequestParam(required = false) String username,
			@RequestParam(required = false) String name, @RequestParam(required = false) String classCode,
			@RequestParam(required = false) String raceCode, @RequestParam(required = false) Integer minLevel,
			@RequestParam(required = false) Integer maxLevel,
			@PageableDefault(size = 20, sort = "level", direction = Sort.Direction.DESC) Pageable pageable) {
		return characterService.list(username, name, classCode, raceCode, minLevel, maxLevel, pageable);
	}

	@GetMapping("/{id}")
	public CharacterDto get(@PathVariable Long id) {
		return characterService.get(id);
	}

	@PostMapping
	public CharacterDto create(@RequestBody @Valid CharacterCreateRequest req) {
		return characterService.create(req);
	}

	@PatchMapping("/{id}")
	public CharacterDto update(@PathVariable Long id, @RequestBody @Valid CharacterUpdateRequest req) {
		return characterService.update(id, req);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		characterService.delete(id);
	}
}

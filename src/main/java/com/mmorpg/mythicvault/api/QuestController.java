package com.mmorpg.mythicvault.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmorpg.mythicvault.dto.QuestCreateRequest;
import com.mmorpg.mythicvault.dto.QuestDto;
import com.mmorpg.mythicvault.service.QuestService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Quests")
@RestController
@RequestMapping("/api/quests")
public class QuestController {

	private final QuestService questService;

	public QuestController(QuestService questService) {
		this.questService = questService;
	}

	@GetMapping
	public Page<QuestDto> list(@RequestParam(required = false) Integer minLevel,
			@RequestParam(required = false) Boolean repeatable,
			@PageableDefault(size = 20, sort = "minLevel") Pageable pageable) {
		return questService.list(minLevel, repeatable, pageable);
	}

	@GetMapping("/{id}")
	public QuestDto get(@PathVariable Long id) {
		return questService.get(id);
	}

	@PostMapping
	public QuestDto create(@RequestBody @Valid QuestCreateRequest req) {
		return questService.create(req);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		questService.delete(id);
	}

}

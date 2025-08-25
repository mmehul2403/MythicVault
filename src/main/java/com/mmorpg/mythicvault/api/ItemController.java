package com.mmorpg.mythicvault.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.mmorpg.mythicvault.dto.ItemCreateRequest;
import com.mmorpg.mythicvault.dto.ItemDto;
import com.mmorpg.mythicvault.dto.ItemUpdateRequest;
import com.mmorpg.mythicvault.service.ItemService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Items")
@RestController
@RequestMapping("/api")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping
	public Page<ItemDto> list(@RequestParam(required = false) String q, @RequestParam(required = false) String rarity,
			@RequestParam(required = false) String type, @RequestParam(required = false) Integer level,
			@PageableDefault(size = 20, sort = "name") Pageable pageable) {
		return itemService.list(q, rarity, type, level, pageable);
	}

	@GetMapping("/items/{id}")
	public ItemDto get(@PathVariable Long id) {
		return itemService.get(id);
	}

	@PostMapping("/items")
	public ItemDto create(@RequestBody @Valid ItemCreateRequest req) {
		return itemService.create(req);
	}

	@PatchMapping("/items/{id}")
	public ItemDto update(@PathVariable Long id, @RequestBody @Valid ItemUpdateRequest req) {
		return itemService.update(id, req);
	}

	@DeleteMapping("/admin/items/{id}")
	public void delete(@PathVariable Long id) {
		itemService.delete(id);
	}

}

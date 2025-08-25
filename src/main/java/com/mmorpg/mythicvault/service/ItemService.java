package com.mmorpg.mythicvault.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mmorpg.mythicvault.dto.ItemCreateRequest;
import com.mmorpg.mythicvault.dto.ItemDto;
import com.mmorpg.mythicvault.dto.ItemUpdateRequest;

public interface ItemService {
    Page<ItemDto> list(String q, String rarity, String type, Integer level, Pageable pageable);

    ItemDto get(Long id);

    ItemDto create(ItemCreateRequest req);

    ItemDto update(Long id, ItemUpdateRequest req);

    void delete(Long id);
}

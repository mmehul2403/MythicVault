package com.mmorpg.mythicvault.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmorpg.mythicvault.dto.ItemCreateRequest;
import com.mmorpg.mythicvault.dto.ItemDto;
import com.mmorpg.mythicvault.dto.ItemUpdateRequest;
import com.mmorpg.mythicvault.entity.Item;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.ItemRepository;
import com.mmorpg.mythicvault.repositorie.ItemTypeRepository;
import com.mmorpg.mythicvault.repositorie.RarityRepository;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepo;
	private final ItemTypeRepository itemTypeRepo;
	private final RarityRepository rarityRepo;

	public ItemServiceImpl(ItemRepository itemRepo, ItemTypeRepository itemTypeRepo, RarityRepository rarityRepo) {
		this.itemRepo = itemRepo;
		this.itemTypeRepo = itemTypeRepo;
		this.rarityRepo = rarityRepo;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ItemDto> list(String q, String rarity, String type, Integer level, Pageable pageable) {
	    String qPattern = null;
	    if (q != null && !q.isBlank()) {
	        qPattern = "%" + q.toLowerCase() + "%";
	    }
	    return itemRepo.search(qPattern, rarity, type, pageable, level)
	                   .map(ItemServiceImpl::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDto get(Long id) {
		var item = itemRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
		return toDto(item);
	}

	@Override
	public ItemDto create(ItemCreateRequest req) {
		var type = itemTypeRepo.findByCode(req.itemTypeCode())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid itemTypeCode"));
		var rarity = rarityRepo.findByCode(req.rarityCode())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid rarityCode"));

		var item = new Item();
		item.setName(req.name());
		item.setDescription(req.description());
		item.setItemType(type);
		item.setRarity(rarity);
		item.setRequiredLevel(Optional.ofNullable(req.requiredLevel()).orElse(1));
		item.setMaxStack(Optional.ofNullable(req.maxStack()).orElse(1));
		item.setBaseValue(Optional.ofNullable(req.baseValue()).orElse(0L));
		item.setBindOnPickup(req.bindOnPickup());
		item.setTradable(req.tradable());

		return toDto(itemRepo.save(item));
	}

	@Override
	public ItemDto update(Long id, ItemUpdateRequest req) {
		var item = itemRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));

		if (req.name() != null)
			item.setName(req.name());
		if (req.description() != null)
			item.setDescription(req.description());

		if (req.itemTypeCode() != null) {
			var type = itemTypeRepo.findByCode(req.itemTypeCode())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid itemTypeCode"));
			item.setItemType(type);
		}

		if (req.rarityCode() != null) {
			var rarity = rarityRepo.findByCode(req.rarityCode())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid rarityCode"));
			item.setRarity(rarity);
		}

		if (req.requiredLevel() != null)
			item.setRequiredLevel(req.requiredLevel());
		if (req.maxStack() != null)
			item.setMaxStack(req.maxStack());
		if (req.baseValue() != null)
			item.setBaseValue(req.baseValue());
		if (req.bindOnPickup() != null)
			item.setBindOnPickup(req.bindOnPickup());
		if (req.tradable() != null)
			item.setTradable(req.tradable());

		return toDto(itemRepo.save(item));
	}

	@Override
	public void delete(Long id) {
		if (!itemRepo.existsById(id))
			throw new ResourceNotFoundException("Item not found: " + id);
		itemRepo.deleteById(id);
	}

	private static ItemDto toDto(Item i) {
		return new ItemDto(i.getId(), i.getName(), i.getDescription(),
				i.getItemType() != null ? i.getItemType().getCode() : null,
				i.getRarity() != null ? i.getRarity().getCode() : null, i.getRequiredLevel(), i.getMaxStack(),
				i.getBaseValue(), i.isBindOnPickup(), i.isTradable(), i.getCreatedAt(), i.getUpdatedAt());
	}
}

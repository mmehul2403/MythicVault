package com.mmorpg.mythicvault.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mmorpg.mythicvault.dto.QuestCreateRequest;
import com.mmorpg.mythicvault.dto.QuestDto;

public interface QuestService {
	Page<QuestDto> list(Integer minLevel, Boolean repeatable, Pageable pageable);

	QuestDto get(Long id);

	QuestDto create(QuestCreateRequest req);

	void delete(Long id);
}

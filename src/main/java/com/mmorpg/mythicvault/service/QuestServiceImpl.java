package com.mmorpg.mythicvault.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmorpg.mythicvault.dto.QuestCreateRequest;
import com.mmorpg.mythicvault.dto.QuestDto;
import com.mmorpg.mythicvault.entity.Quest;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.QuestRepository;
import com.mmorpg.mythicvault.spec.QuestSpecs;

@Service
@Transactional
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepo;

	QuestServiceImpl(QuestRepository questRepo) {
		this.questRepo = questRepo;
	}

	@Transactional(readOnly = true)
	public Page<QuestDto> list(Integer minLevel, Boolean repeatable, Pageable pageable) {
		var spec = QuestSpecs.minLevelLessOrEqual(minLevel).and(QuestSpecs.repeatable(repeatable));
		return questRepo.findAll(spec, pageable).map(QuestServiceImpl::toDto);
	}

	@Transactional(readOnly = true)
	public QuestDto get(Long id) {
		var q = questRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Quest not found: " + id));
		return toDto(q);
	}

	public QuestDto create(QuestCreateRequest req) {
		var q = new Quest();
		q.setTitle(req.title());
		q.setSummary(req.summary());
		q.setMinLevel(req.minLevel() != null ? req.minLevel() : 1);
		q.setRepeatable(req.repeatable() != null ? req.repeatable() : false);
		return toDto(questRepo.save(q));
	}

	public void delete(Long id) {
		if (!questRepo.existsById(id))
			throw new ResourceNotFoundException("Quest not found: " + id);
		questRepo.deleteById(id);
	}

	static QuestDto toDto(Quest q) {
		return new QuestDto(q.getId(), q.getTitle(), q.getSummary(), q.getMinLevel(), q.isRepeatable());
	}
}

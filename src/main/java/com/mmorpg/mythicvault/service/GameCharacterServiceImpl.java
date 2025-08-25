package com.mmorpg.mythicvault.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmorpg.mythicvault.dto.CharacterCreateRequest;
import com.mmorpg.mythicvault.dto.CharacterDto;
import com.mmorpg.mythicvault.dto.CharacterUpdateRequest;
import com.mmorpg.mythicvault.entity.GameCharacter;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.AccountUserRepository;
import com.mmorpg.mythicvault.repositorie.CharacterClassRepository;
import com.mmorpg.mythicvault.repositorie.GameCharacterRepository;
import com.mmorpg.mythicvault.repositorie.RaceRepository;
import com.mmorpg.mythicvault.spec.GameCharacterSpecs;

@Service
@Transactional
public class GameCharacterServiceImpl implements GameCharacterService {

	private final GameCharacterRepository charRepo;
	private final AccountUserRepository accountRepo;
	private final CharacterClassRepository classRepo;
	private final RaceRepository raceRepo;

	GameCharacterServiceImpl(GameCharacterRepository charRepo, AccountUserRepository accountRepo,
			CharacterClassRepository classRepo, RaceRepository raceRepo) {
		this.charRepo = charRepo;
		this.accountRepo = accountRepo;
		this.classRepo = classRepo;
		this.raceRepo = raceRepo;
	}

	@Transactional(readOnly = true)
	public Page<CharacterDto> list(String username, String name, String classCode, String raceCode, Integer minLevel,
			Integer maxLevel, Pageable pageable) {
		var spec = GameCharacterSpecs.usernameEquals(username).and(GameCharacterSpecs.nameContains(name))
				.and(GameCharacterSpecs.classCode(classCode)).and(GameCharacterSpecs.raceCode(raceCode))
				.and(GameCharacterSpecs.levelBetween(minLevel, maxLevel));
		return charRepo.findAll(spec, pageable).map(GameCharacterServiceImpl::toDto);
	}

	@Transactional(readOnly = true)
	public CharacterDto get(Long id) {
		var c = charRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Character not found: " + id));
		return toDto(c);
	}

	public CharacterDto create(CharacterCreateRequest req) {
		var account = accountRepo.findByUsername(req.username())
				.orElseThrow(() -> new ResourceNotFoundException("Unknown username"));
		var clazz = classRepo.findByCode(req.classCode())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid classCode"));
		var race = raceRepo.findByCode(req.raceCode())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid raceCode"));

		var c = new GameCharacter();
		c.setAccount(account);
		c.setName(req.name());
		c.setCharacterClass(clazz);
		c.setRace(race);
		c.setLevel(Optional.ofNullable(req.level()).orElse(1));
		c.setExperience(0L);
		c.setGold(0L);
		return toDto(charRepo.save(c));
	}

	public CharacterDto update(Long id, CharacterUpdateRequest req) {
		var c = charRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Character not found: " + id));
		if (req.name() != null)
			c.setName(req.name());
		if (req.classCode() != null)
			c.setCharacterClass(classRepo.findByCode(req.classCode())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid classCode")));
		if (req.raceCode() != null)
			c.setRace(raceRepo.findByCode(req.raceCode())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid raceCode")));
		if (req.level() != null)
			c.setLevel(req.level());
		if (req.experience() != null)
			c.setExperience(req.experience());
		if (req.gold() != null)
			c.setGold(req.gold());
		return toDto(charRepo.save(c));
	}

	public void delete(Long id) {
		if (!charRepo.existsById(id))
			throw new ResourceNotFoundException("Character not found: " + id);
		charRepo.deleteById(id);
	}

	static CharacterDto toDto(GameCharacter c) {
		return new CharacterDto(c.getId(), c.getAccount() != null ? c.getAccount().getUsername() : null, c.getName(),
				c.getCharacterClass() != null ? c.getCharacterClass().getCode() : null,
				c.getRace() != null ? c.getRace().getCode() : null, c.getLevel(), c.getExperience(), c.getGold());
	}
}

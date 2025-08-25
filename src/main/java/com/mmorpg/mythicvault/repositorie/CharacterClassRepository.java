package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.CharacterClass;

@Repository
public interface CharacterClassRepository extends JpaRepository<CharacterClass, Short> {
	Optional<CharacterClass> findByCode(String code);
}
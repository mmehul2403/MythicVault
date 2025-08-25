package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Race;

@Repository
public interface RaceRepository extends JpaRepository<Race, Short> {
	Optional<Race> findByCode(String code);
}
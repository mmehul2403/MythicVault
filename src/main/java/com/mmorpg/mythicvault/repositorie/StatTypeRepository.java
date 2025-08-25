package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.StatType;

@Repository
public interface StatTypeRepository extends JpaRepository<StatType, Short> {
	Optional<StatType> findByCode(String code);
}

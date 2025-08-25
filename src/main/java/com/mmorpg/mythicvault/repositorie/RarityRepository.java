package com.mmorpg.mythicvault.repositorie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Rarity;

@Repository
public interface RarityRepository extends JpaRepository<Rarity, Short> {

	Optional<Rarity> findByCode(String code);

	List<Rarity> findAllByOrderBySortOrderAsc();
}
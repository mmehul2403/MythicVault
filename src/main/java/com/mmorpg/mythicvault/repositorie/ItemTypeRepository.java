package com.mmorpg.mythicvault.repositorie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.ItemType;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Short> {
	Optional<ItemType> findByCode(String code);

	List<ItemType> findAllByOrderBySortOrderAsc();
}
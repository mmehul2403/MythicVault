package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.ItemInstance;

@Repository
public interface ItemInstanceRepository extends JpaRepository<ItemInstance, Long> {
	List<ItemInstance> findByItemId(Long itemId);

	List<ItemInstance> findByBoundToId(Long characterId);
}
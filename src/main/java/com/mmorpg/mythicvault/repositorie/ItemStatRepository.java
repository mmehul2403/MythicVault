package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.ItemStat;
import com.mmorpg.mythicvault.entity.ItemStatId;


@Repository
public interface ItemStatRepository extends JpaRepository<ItemStat, ItemStatId> {
	List<ItemStat> findByItemId(Long itemId);

	List<ItemStat> findByStatTypeCode(String code);

	@Query("""
			select is from ItemStat is
			join fetch is.statType st
			where is.item.id = :itemId
			""")
	List<ItemStat> fetchByItemId(@Param("itemId") Long itemId);
}
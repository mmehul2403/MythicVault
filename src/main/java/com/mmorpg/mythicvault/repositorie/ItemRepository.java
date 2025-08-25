package com.mmorpg.mythicvault.repositorie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

	@Override
	@EntityGraph(attributePaths = { "itemType", "rarity" })
	Page<Item> findAll(@org.springframework.lang.Nullable Specification<Item> spec, Pageable pageable);

	@EntityGraph(attributePaths = { "itemType", "rarity" })
	@Query("""
			select i from Item i
			join i.itemType it
			join i.rarity r
			where (:qPattern is null or lower(i.name) like :qPattern)
			  and (:rarity is null or r.code = :rarity)
			  and (:type   is null or it.code = :type)
			  and (:maxUsableLevel is null or i.requiredLevel <= :maxUsableLevel)
			""")
	Page<Item> search(@Param("qPattern") String qPattern, @Param("rarity") String rarityCode,
			@Param("type") String typeCode, Pageable pageable, @Param("maxUsableLevel") Integer level);

	@EntityGraph(attributePaths = { "itemType", "rarity" })
	Page<Item> findByRequiredLevelLessThanEqual(int level, Pageable pageable);
}

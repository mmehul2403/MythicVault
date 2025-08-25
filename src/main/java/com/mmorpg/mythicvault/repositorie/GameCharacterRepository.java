package com.mmorpg.mythicvault.repositorie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.GameCharacter;

@Repository
public interface GameCharacterRepository
		extends JpaRepository<GameCharacter, Long>, JpaSpecificationExecutor<GameCharacter> {

	List<GameCharacter> findByAccountUsername(String username);

	@EntityGraph(attributePaths = { "characterClass", "race" })
	Page<GameCharacter> findByLevelBetween(int min, int max, Pageable pageable);

	Optional<GameCharacter> findByAccountUsernameAndName(String username, String name);

	// JPQL query
	@Query("""
			select gc from GameCharacter gc
			join fetch gc.characterClass cc
			join fetch gc.race r
			where gc.account.username = :username and lower(gc.name) = lower(:name)
			""")
	Optional<GameCharacter> fetchByUserAndName(@Param("username") String username, @Param("name") String name);
}
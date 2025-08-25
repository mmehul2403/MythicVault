package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.Guild;


@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {

	Optional<Guild> findByName(String name);

	@EntityGraph(attributePaths = { "leader" })
	Optional<Guild> findOneWithLeaderById(Long id);
}
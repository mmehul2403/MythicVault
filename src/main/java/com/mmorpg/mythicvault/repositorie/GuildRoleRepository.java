package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.GuildRole;


@Repository
public interface GuildRoleRepository extends JpaRepository<GuildRole, Short> {
    Optional<GuildRole> findByCode(String code);
}
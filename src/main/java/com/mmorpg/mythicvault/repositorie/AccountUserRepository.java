package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.AccountUser;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
	Optional<AccountUser> findByUsername(String username);
	Optional<AccountUser> findByEmail(String email);
}
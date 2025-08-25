package com.mmorpg.mythicvault.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mmorpg.mythicvault.repositorie.AccountUserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	private final AccountUserRepository repo;

	public JpaUserDetailsService(AccountUserRepository repo) {
		this.repo = repo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new UserAdapter(user);
	}
}

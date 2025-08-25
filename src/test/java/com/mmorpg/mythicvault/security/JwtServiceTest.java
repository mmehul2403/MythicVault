package com.mmorpg.mythicvault.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.mmorpg.mythicvault.entity.AccountUser;

@ActiveProfiles("test")
public class JwtServiceTest {

	private JwtService jwt;

	@BeforeEach
	void setup() {
		jwt = new JwtService();
		// inject via reflection since @Value isn't active here
		var secret = "b938c4b0c3a64c07a1190b7f7eefc1c5b938c4b0c3a64c07a1190b7f7eefc1c5";
		try {
			var f1 = JwtService.class.getDeclaredField("secret");
			f1.setAccessible(true);
			f1.set(jwt, secret);
			var f2 = JwtService.class.getDeclaredField("ttlMillis");
			f2.setAccessible(true);
			f2.set(jwt, 3600000L);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void generate_and_parse() {
		var u = new AccountUser();
		u.setUsername("demo_user");
		u.setRoles(Set.of("ROLE_USER"));
		u.setCreatedAt(OffsetDateTime.now());
		u.setUpdatedAt(OffsetDateTime.now());

		var token = jwt.generateToken(u);
		assertThat(token).isNotBlank();

		var username = jwt.extractUsername(token);
		assertThat(username).isEqualTo("demo_user");
	}
}

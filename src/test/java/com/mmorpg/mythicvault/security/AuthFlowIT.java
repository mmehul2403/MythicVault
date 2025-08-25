package com.mmorpg.mythicvault.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmorpg.mythicvault.entity.AccountUser;
import com.mmorpg.mythicvault.repositorie.AccountUserRepository;

/*
 * Integration test: end-to-end auth + security
 * */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthFlowIT {

	private final MockMvc mvc;
	private final ObjectMapper om;
	private final AccountUserRepository userRepo;
	private final PasswordEncoder encoder;

	AuthFlowIT(MockMvc mvc, ObjectMapper om, AccountUserRepository userRepo, PasswordEncoder encoder) {
		this.mvc = mvc;
		this.om = om;
		this.userRepo = userRepo;
		this.encoder = encoder;
	}

	@Test
	void unauthenticated_api_should_return_401() throws Exception {
		mvc.perform(get("/api/test/protected")).andExpect(status().isUnauthorized());
	}

	@Test
	void login_then_access_protected_with_bearer() throws Exception {
		// Arrange: ensure a user exists
		var u = userRepo.findByUsername("demo_user").orElseGet(() -> {
			var nu = new AccountUser();
			nu.setUsername("demo_user");
			nu.setEmail("demo@example.com");
			nu.setPasswordHash(encoder.encode("password"));
			nu.setRoles(Set.of("ROLE_USER"));
			nu.setCreatedAt(OffsetDateTime.now());
			nu.setUpdatedAt(OffsetDateTime.now());
			return userRepo.save(nu);
		});

		// Act: login
		var loginJson = om.writeValueAsString(Map.of("username", "demo_user", "password", "password"));
		var resp = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isOk()).andReturn();

		var body = resp.getResponse().getContentAsString();
		var token = om.readTree(body).get("token").asText();
		assertThat(token).isNotBlank();

		// Assert: protected endpoint with bearer succeeds
		mvc.perform(get("/api/test/protected").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(content().json("{\"ok\":true}"));
	}

	/** Test-only protected controller to avoid coupling tests to real endpoints */
	@TestConfiguration
	static class TestProtectedControllerConfig {
		@Bean
		TestProtectedController testProtectedController() {
			return new TestProtectedController();
		}
	}

	@RestController
	static class TestProtectedController {
		@GetMapping("/api/test/protected")
		public Map<String, Object> protectedOk() {
			return Map.of("ok", true);
		}
	}

}

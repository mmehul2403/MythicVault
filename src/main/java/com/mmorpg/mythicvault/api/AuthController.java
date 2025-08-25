package com.mmorpg.mythicvault.api;

import java.time.OffsetDateTime;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mmorpg.mythicvault.entity.AccountUser;
import com.mmorpg.mythicvault.repositorie.AccountUserRepository;
import com.mmorpg.mythicvault.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

	private final AuthenticationManager authManager;
	private final JwtService jwtService;
	private final AccountUserRepository userRepo;
	private final PasswordEncoder encoder;

	public AuthController(AuthenticationManager authManager, JwtService jwtService, AccountUserRepository userRepo,
			PasswordEncoder encoder) {
		this.authManager = authManager;
		this.jwtService = jwtService;
		this.userRepo = userRepo;
		this.encoder = encoder;
	}

	public record LoginRequest(String username, String password) {
	}

	public record AuthResponse(String token, String tokenType) {
	}

	public record RegisterRequest(String username, String email, String password) {
	}

	@PostMapping("/login")
	@Operation(summary = "Login and get JWT", security = {}) // no bearer needed
	public AuthResponse login(@RequestBody LoginRequest req) {
		var auth = new UsernamePasswordAuthenticationToken(req.username(), req.password());
		authManager.authenticate(auth);
		var user = userRepo.findByUsername(req.username()).orElseThrow();
		var token = jwtService.generateToken(user);
		return new AuthResponse(token, "Bearer");
	}

	@PostMapping("/register")
	@Operation(summary = "Register (dev/demo)", security = {})
	public AuthResponse register(@RequestBody RegisterRequest req) {
		if (userRepo.findByUsername(req.username()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		var u = new AccountUser();
		u.setUsername(req.username());
		u.setEmail(req.email());
		u.setPasswordHash(encoder.encode(req.password()));
		u.setRoles(Set.of("ROLE_USER"));
		u.setCreatedAt(OffsetDateTime.now());
		u.setUpdatedAt(OffsetDateTime.now());
		userRepo.save(u);
		return new AuthResponse(jwtService.generateToken(u), "Bearer");
	}
}

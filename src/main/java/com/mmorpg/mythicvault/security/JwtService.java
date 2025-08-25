package com.mmorpg.mythicvault.security;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mmorpg.mythicvault.entity.AccountUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${app.jwt.secret}")
	private String secret; 

	@Value("${app.jwt.ttlMillis:3600000}")
	private long ttlMillis; 

	public String generateToken(AccountUser user) {
		long now = System.currentTimeMillis();
		return Jwts.builder().setSubject(user.getUsername()).addClaims(Map.of("roles", user.getRoles() 
		)).setIssuedAt(new Date(now)).setExpiration(new Date(now + ttlMillis))
				.signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();
	}

	public Jws<Claims> parse(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token);
	}

	public String extractUsername(String token) {
		return parse(token).getBody().getSubject();
	}
}

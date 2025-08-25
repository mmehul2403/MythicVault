package com.mmorpg.mythicvault.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwt;
	private final JpaUserDetailsService uds;

	public JwtAuthFilter(JwtService jwt, JpaUserDetailsService uds) {
		this.jwt = jwt;
		this.uds = uds;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		try {
			String header = req.getHeader("Authorization");
			if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
				String token = header.substring(7);
				String username = jwt.extractUsername(token);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					var userDetails = uds.loadUserByUsername(username);
					// Optionally re-parse & verify roles/claims here
					var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (ExpiredJwtException ex) {
			// Optionally set 401 with a message
		} catch (Exception ignored) {
		}

		chain.doFilter(req, res);
	}

	@Override
	protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
		String p = request.getServletPath();
		return p.startsWith("/auth/") || p.startsWith("/v3/api-docs") || p.startsWith("/swagger-ui")
				|| p.equals("/swagger-ui.html") || p.startsWith("/actuator/health");
	}
}

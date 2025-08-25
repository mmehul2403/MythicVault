package com.mmorpg.mythicvault.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI mythicVaultOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("MythicVault API")
						.description("MMORPG catalog & questing API for Portfolio+ tech assessment").version("v1")
						.license(new License().name("Apache-2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
				.servers(List.of(new Server().url("/")))
				.tags(List.of(new Tag().name("Items").description("Items & stats"),
						new Tag().name("Characters").description("Player characters"),
						new Tag().name("leaderboard").description("leaderboards"),
						new Tag().name("Quests").description("Quest catalog")))
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}

	@Bean
	public GroupedOpenApi itemsApi() {
		return GroupedOpenApi.builder().group("items").packagesToScan("com.mmorpg.mythicvault.api")
				.pathsToMatch("/api/items/**", "/api/admin/items/**")
				.build();
	}

	@Bean
	public GroupedOpenApi charactersApi() {
		return GroupedOpenApi.builder().group("characters").packagesToScan("com.mmorpg.mythicvault.api")
				.pathsToMatch("/api/characters/**").build();
	}

	@Bean
	public GroupedOpenApi LeaderBoardApi() {
		return GroupedOpenApi.builder().group("leaderboards").packagesToScan("com.mmorpg.mythicvault.api")
				.pathsToMatch("/api/leaderboards/**").build();
	}

	@Bean
	public GroupedOpenApi questsApi() {
		return GroupedOpenApi.builder().group("quests").packagesToScan("com.mmorpg.mythicvault.api")
				.pathsToMatch("/api/quests/**").build();
	}

	@Bean
	public GroupedOpenApi authApi() {
		return GroupedOpenApi.builder().group("auth").packagesToScan("com.mmorpg.mythicvault.api")
				.pathsToMatch("/auth/**").build();
	}
}

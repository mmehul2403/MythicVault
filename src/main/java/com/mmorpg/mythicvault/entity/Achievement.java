package com.mmorpg.mythicvault.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "achievement")
public class Achievement {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(nullable = false, unique = true)
	private String code;


	@Column(nullable = false)
	private String title;


	@Column(columnDefinition = "text")
	private String description;


	@Column(nullable = false)
	private Integer points = 10;


	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;
}

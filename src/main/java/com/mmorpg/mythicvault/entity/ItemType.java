package com.mmorpg.mythicvault.entity;

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
@Table(name = "item_type")
public class ItemType {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;


	@Column(nullable = false, unique = true)
	private String code;


	@Column(name = "sort_order", nullable = false)
	private Short sortOrder = 0;
	
}

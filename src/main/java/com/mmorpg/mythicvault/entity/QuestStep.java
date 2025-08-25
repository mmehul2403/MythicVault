package com.mmorpg.mythicvault.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quest_step", uniqueConstraints = @UniqueConstraint(name = "uk_quest_step_unique", columnNames = {
		"quest_id", "step_no" }))
public class QuestStep {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "quest_id", nullable = false)
	private Quest quest;

	@Column(name = "step_no", nullable = false)
	private Integer stepNo;

	@Column(nullable = false, columnDefinition = "text")
	private String description;

	@OneToMany(mappedBy = "questStep", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<QuestStepRequirement> requirements = new HashSet<>();
}

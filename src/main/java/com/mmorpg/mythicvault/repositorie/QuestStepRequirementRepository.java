package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.QuestStepRequirement;


@Repository
public interface QuestStepRequirementRepository extends JpaRepository<QuestStepRequirement, Long> {
	List<QuestStepRequirement> findByQuestStepId(Long questStepId);
}

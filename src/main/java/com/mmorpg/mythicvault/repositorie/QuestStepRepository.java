package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.QuestStep;

@Repository
public interface QuestStepRepository extends JpaRepository<QuestStep, Long> {
	List<QuestStep> findByQuestIdOrderByStepNoAsc(Long questId);
}
package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.QuestReward;

@Repository
public interface QuestRewardRepository extends JpaRepository<QuestReward, Long> {
    List<QuestReward> findByQuestId(Long questId);
}
package com.mmorpg.mythicvault.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.RequirementType;


@Repository
public interface RequirementTypeRepository extends JpaRepository<RequirementType, Short> {
	RequirementType findByCode(String code);
}
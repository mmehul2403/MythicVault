package com.mmorpg.mythicvault.repositorie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.EquipmentSlot;

@Repository
public interface EquipmentSlotRepository extends JpaRepository<EquipmentSlot, Short> {
	Optional<EquipmentSlot> findByCode(String code);
}

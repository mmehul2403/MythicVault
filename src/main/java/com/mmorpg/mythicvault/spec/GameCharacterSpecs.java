package com.mmorpg.mythicvault.spec;


import org.springframework.data.jpa.domain.Specification;

import com.mmorpg.mythicvault.entity.GameCharacter;

public final class GameCharacterSpecs {
	private GameCharacterSpecs() {
	}

	public static Specification<GameCharacter> usernameEquals(String username) {
		return (root, q, cb) -> username == null ? null : cb.equal(root.get("account").get("username"), username);
	}

	public static Specification<GameCharacter> nameContains(String fragment) {
		return (root, q, cb) -> fragment == null ? null
				: cb.like(cb.lower(root.get("name")), "%" + fragment.toLowerCase() + "%");
	}

	public static Specification<GameCharacter> classCode(String code) {
		return (root, q, cb) -> code == null ? null : cb.equal(root.get("characterClass").get("code"), code);
	}

	public static Specification<GameCharacter> raceCode(String code) {
		return (root, q, cb) -> code == null ? null : cb.equal(root.get("race").get("code"), code);
	}

	public static Specification<GameCharacter> levelBetween(Integer min, Integer max) {
		return (root, q, cb) -> {
			if (min == null && max == null)
				return null;
			if (min != null && max != null)
				return cb.between(root.get("level"), min, max);
			return (min != null) ? cb.ge(root.get("level"), min) : cb.le(root.get("level"), max);
		};
	}
}

package com.mmorpg.mythicvault.spec;

import org.springframework.data.jpa.domain.Specification;

import com.mmorpg.mythicvault.entity.Item;

public final class ItemSpecs {
    private ItemSpecs() {}

    public static Specification<Item> nameContains(String q) {
        return (root, query, cb) -> q == null ? null
                : cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%");
    }

    public static Specification<Item> rarity(String rarityCode) {
        return (root, query, cb) -> rarityCode == null ? null
                : cb.equal(root.get("rarity").get("code"), rarityCode);
    }

    public static Specification<Item> type(String typeCode) {
        return (root, query, cb) -> typeCode == null ? null
                : cb.equal(root.get("itemType").get("code"), typeCode);
    }

    public static Specification<Item> usableByLevel(Integer level) {
        return (root, query, cb) -> level == null ? null
                : cb.le(root.get("requiredLevel"), level);
    }
}

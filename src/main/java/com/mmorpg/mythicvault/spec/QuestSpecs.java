package com.mmorpg.mythicvault.spec;

import org.springframework.data.jpa.domain.Specification;

import com.mmorpg.mythicvault.entity.Quest;

public final class QuestSpecs {
    private QuestSpecs() {}

    public static Specification<Quest> minLevelLessOrEqual(Integer level) {
        return (root, q, cb) -> level == null ? null
                : cb.le(root.get("minLevel"), level);
    }

    public static Specification<Quest> repeatable(Boolean repeatable) {
        return (root, q, cb) -> repeatable == null ? null
                : (repeatable ? cb.isTrue(root.get("repeatable")) : cb.isFalse(root.get("repeatable")));
    }
}

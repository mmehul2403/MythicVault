-- V3__sample_data.sql
-- Sample data for MMORPG catalog (idempotent where practical)

-- =========================================================
-- Accounts & Characters
-- =========================================================
INSERT INTO account_user (username, email, password_hash, created_at, updated_at)
VALUES (
    'demo_user',
    'demo@example.com',
    '$2a$10$0.lwB4WGV1C2hYuFbGugSuKdVyJvuSs7Mr1FPZZxyg2XsGn2eBW5C', --test
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO account_user (username, email, password_hash, created_at, updated_at)
VALUES (
    'admin_user',
    'admin@example.com',
    '$2a$10$0.lwB4WGV1C2hYuFbGugSuKdVyJvuSs7Mr1FPZZxyg2XsGn2eBW5C', --test
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO account_user_roles (user_id, role)
SELECT id, 'ROLE_USER'
FROM account_user WHERE username = 'demo_user'
ON CONFLICT DO NOTHING;

INSERT INTO account_user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN'
FROM account_user WHERE username = 'admin_user'
ON CONFLICT DO NOTHING;

INSERT INTO account_user_roles (user_id, role)
SELECT id, 'ROLE_USER'
FROM account_user WHERE username = 'admin_user'
ON CONFLICT DO NOTHING;


WITH au AS (
  SELECT id AS account_id FROM account_user WHERE username = 'demo_user'
),
cls AS (
  SELECT id AS class_id FROM class WHERE code = 'warrior'
),
rc AS (
  SELECT id AS race_id FROM race WHERE code = 'human'
)
INSERT INTO character (account_id, name, class_id, race_id, level, experience, gold)
SELECT au.account_id, 'Thorin', cls.class_id, rc.race_id, 8, 12345, 250
FROM au, cls, rc
ON CONFLICT (account_id, name) DO NOTHING;

-- =========================================================
-- Items
-- =========================================================
-- Steel Longsword (weapon, rare)
WITH it AS (SELECT id AS item_type_id FROM item_type WHERE code = 'weapon'),
     rar AS (SELECT id AS rarity_id FROM rarity WHERE code = 'rare')
INSERT INTO item (name, description, item_type_id, rarity_id, required_level, max_stack, base_value, bind_on_pickup, tradable)
SELECT 'Steel Longsword', 'Reliable blade', it.item_type_id, rar.rarity_id, 5, 1, 150, TRUE, FALSE
FROM it, rar
ON CONFLICT DO NOTHING;

-- Health Potion (consumable, common)
WITH it AS (SELECT id AS item_type_id FROM item_type WHERE code = 'consumable'),
     rar AS (SELECT id AS rarity_id FROM rarity WHERE code = 'common')
INSERT INTO item (name, description, item_type_id, rarity_id, required_level, max_stack, base_value, bind_on_pickup, tradable)
SELECT 'Health Potion', 'Restores 250 HP', it.item_type_id, rar.rarity_id, 1, 20, 25, FALSE, TRUE
FROM it, rar
ON CONFLICT DO NOTHING;

-- Item stat for the sword: +12 attack
WITH sword AS (SELECT id AS item_id FROM item WHERE name = 'Steel Longsword'),
     st AS (SELECT id AS stat_type_id FROM stat_type WHERE code = 'attack')
INSERT INTO item_stat (item_id, stat_type_id, value)
SELECT sword.item_id, st.stat_type_id, 12
FROM sword, st
ON CONFLICT (item_id, stat_type_id) DO NOTHING;

-- =========================================================
-- Character inventory & equipment
-- =========================================================
-- Give Thorin 5 health potions (stackable upsert)
WITH ch AS (SELECT id AS character_id FROM character WHERE name = 'Thorin'),
     potion AS (SELECT id AS item_id FROM item WHERE name = 'Health Potion')
INSERT INTO character_inventory (character_id, item_id, quantity)
SELECT ch.character_id, potion.item_id, 5
FROM ch, potion
ON CONFLICT (character_id, item_id)
DO UPDATE SET quantity = character_inventory.quantity + EXCLUDED.quantity;

-- Create a unique sword instance bound to Thorin and equip in mainhand
WITH ch AS (
  SELECT id AS character_id FROM character WHERE name = 'Thorin'
),
sword AS (
  SELECT id AS item_id FROM item WHERE name = 'Steel Longsword'
),
inst AS (
  INSERT INTO item_instance (item_id, bound_to_char, durability)
  SELECT sword.item_id, ch.character_id, 100
  FROM sword, ch
  RETURNING id AS item_instance_id, item_id, bound_to_char
),
slot AS (
  SELECT id AS slot_id FROM equipment_slot WHERE code = 'mainhand'
)
INSERT INTO character_equipment (character_id, equipment_slot_id, item_instance_id)
SELECT ch.character_id, slot.slot_id, inst.item_instance_id
FROM ch, slot, inst
ON CONFLICT (character_id, equipment_slot_id)
DO UPDATE SET item_instance_id = EXCLUDED.item_instance_id;

-- =========================================================
-- Quest: First Aid (collect herbs) + reward
-- =========================================================
-- Create quest
INSERT INTO quest (title, summary, min_level, repeatable)
VALUES ('First Aid', 'Collect herbs for the healer', 1, FALSE)
ON CONFLICT DO NOTHING;

-- Step 1
WITH q AS (
  SELECT id AS quest_id FROM quest WHERE title = 'First Aid'
)
INSERT INTO quest_step (quest_id, step_no, description)
SELECT q.quest_id, 1, 'Collect 3 healing herbs'
FROM q
ON CONFLICT (quest_id, step_no) DO NOTHING;

-- Requirement: collect 3 x herb_green
WITH qs AS (
  SELECT qs.id AS quest_step_id
  FROM quest q
  JOIN quest_step qs ON qs.quest_id = q.id AND qs.step_no = 1
  WHERE q.title = 'First Aid'
),
rt AS (
  SELECT id AS requirement_type_id FROM requirement_type WHERE code = 'collect'
)
INSERT INTO quest_step_requirement (quest_step_id, requirement_type_id, target_ref, target_count)
SELECT qs.quest_step_id, rt.requirement_type_id, 'herb_green', 3
FROM qs, rt
ON CONFLICT DO NOTHING;

-- Reward: 2 health potions + 200 XP
WITH q AS (SELECT id AS quest_id FROM quest WHERE title = 'First Aid'),
     potion AS (SELECT id AS item_id FROM item WHERE name = 'Health Potion')
INSERT INTO quest_reward (quest_id, item_id, item_qty, experience, gold)
SELECT q.quest_id, potion.item_id, 2, 200, 0
FROM q, potion
ON CONFLICT DO NOTHING;

-- Thorin accepts the quest
WITH ch AS (SELECT id AS character_id FROM character WHERE name = 'Thorin'),
     q  AS (SELECT id AS quest_id FROM quest WHERE title = 'First Aid')
INSERT INTO character_quest (character_id, quest_id)
SELECT ch.character_id, q.quest_id
FROM ch, q
ON CONFLICT (character_id, quest_id) DO NOTHING;

-- Optionally seed a little progress (comment out if undesired)
-- WITH ch AS (SELECT id AS character_id FROM character WHERE name = 'Thorin'),
--      qs AS (
--        SELECT qs.id AS quest_step_id
--        FROM quest q JOIN quest_step qs ON qs.quest_id = q.id AND qs.step_no = 1
--        WHERE q.title = 'First Aid'
--      )
-- INSERT INTO character_quest_step_progress (character_id, quest_step_id, progress_count)
-- SELECT ch.character_id, qs.quest_step_id, 1
-- FROM ch, qs
-- ON CONFLICT (character_id, quest_step_id) DO UPDATE SET progress_count = EXCLUDED.progress_count;

-- =========================================================
-- (Optional) Achievement seed & unlock
-- =========================================================
INSERT INTO achievement (code, title, description, points)
VALUES ('first_steps', 'First Steps', 'Complete your first quest', 10)
ON CONFLICT (code) DO NOTHING;

WITH ch AS (SELECT id AS character_id FROM character WHERE name = 'Thorin'),
     ach AS (SELECT id AS achievement_id FROM achievement WHERE code = 'first_steps')
INSERT INTO character_achievement (character_id, achievement_id)
SELECT ch.character_id, ach.achievement_id
FROM ch, ach
ON CONFLICT (character_id, achievement_id) DO NOTHING;

-- =========================================================
-- (Optional) Leaderboard snapshot sample
-- =========================================================
WITH lbt AS (SELECT id AS leaderboard_type_id FROM leaderboard_type WHERE code = 'level'),
     snap AS (
       INSERT INTO leaderboard_snapshot (leaderboard_type_id)
       SELECT leaderboard_type_id FROM lbt
       RETURNING id AS snapshot_id
     ),
     ch AS (
       SELECT id AS character_id, level::bigint AS score_value
       FROM character
       WHERE name = 'Thorin'
     )
INSERT INTO leaderboard_entry (snapshot_id, rank, character_id, score_value)
SELECT snap.snapshot_id, 1, ch.character_id, ch.score_value
FROM snap, ch
ON CONFLICT DO NOTHING;

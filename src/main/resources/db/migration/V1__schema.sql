-- V1__schema.sql
-- Baseline normalized schema for MMORPG catalog
-- Postgres specific: uses GENERATED ALWAYS AS IDENTITY and timestamptz

-- 1) Utility: updated_at auto-touch
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END;
$$;

-- 2) Reference/lookups (3NF-friendly; easy to extend)

CREATE TABLE rarity (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- common, rare, epic, legendary
  sort_order   SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE item_type (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- weapon, armor, consumable, material, quest_item, trinket
  sort_order   SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE stat_type (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- attack, defense, agility, intellect, stamina, crit_chance, etc.
  unit         TEXT NOT NULL DEFAULT 'points'
);

CREATE TABLE equipment_slot (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- head, chest, legs, feet, hands, ring, amulet, mainhand, offhand, belt, shoulders, back
  sort_order   SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE class (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- warrior, mage, rogue, ranger, cleric...
  description  TEXT
);

CREATE TABLE race (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- human, elf, orc, dwarf...
  description  TEXT
);

CREATE TABLE guild_role (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- leader, officer, member
  sort_order   SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE leaderboard_type (
  id           SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code         TEXT NOT NULL UNIQUE,        -- level, achievements, pvp_rating, wealth
  description  TEXT
);

-- 3) Accounts & characters (players can own multiple characters)
-- If you don’t need accounts, you can operate only with character.

CREATE TABLE account_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE account_user_roles (
    user_id BIGINT NOT NULL REFERENCES account_user(id) ON DELETE CASCADE,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TRIGGER trg_account_user_uat BEFORE UPDATE ON account_user
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE character (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  account_id    BIGINT NOT NULL REFERENCES account_user(id) ON DELETE CASCADE,
  name          TEXT NOT NULL,
  class_id      SMALLINT NOT NULL REFERENCES class(id),
  race_id       SMALLINT NOT NULL REFERENCES race(id),
  level         INTEGER NOT NULL DEFAULT 1 CHECK (level >= 1),
  experience    BIGINT  NOT NULL DEFAULT 0 CHECK (experience >= 0),
  gold          BIGINT  NOT NULL DEFAULT 0 CHECK (gold >= 0),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (account_id, name)
);
CREATE INDEX idx_character_account ON character(account_id);
CREATE INDEX idx_character_class ON character(class_id);
CREATE INDEX idx_character_race  ON character(race_id);
CREATE TRIGGER trg_character_uat BEFORE UPDATE ON character
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 4) Items & stats

CREATE TABLE item (
  id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name            TEXT NOT NULL,
  description     TEXT,
  item_type_id    SMALLINT NOT NULL REFERENCES item_type(id),
  rarity_id       SMALLINT NOT NULL REFERENCES rarity(id),
  required_level  INTEGER  NOT NULL DEFAULT 1 CHECK (required_level >= 1),
  max_stack       INTEGER  NOT NULL DEFAULT 1 CHECK (max_stack >= 1),
  base_value      BIGINT   NOT NULL DEFAULT 0 CHECK (base_value >= 0), -- vendor price
  bind_on_pickup  BOOLEAN  NOT NULL DEFAULT FALSE,
  tradable        BOOLEAN  NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_item_type   ON item(item_type_id);
CREATE INDEX idx_item_rarity ON item(rarity_id);
CREATE TRIGGER trg_item_uat BEFORE UPDATE ON item
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Each item can have multiple stats (e.g., +10 attack, +5 stamina)
CREATE TABLE item_stat (
  item_id      BIGINT    NOT NULL REFERENCES item(id) ON DELETE CASCADE,
  stat_type_id SMALLINT  NOT NULL REFERENCES stat_type(id),
  value        DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (item_id, stat_type_id)
);

-- 5) Inventory & Equipment
-- Normalize stackable vs. non-stackable:
-- - stackables live in character_inventory (with quantity)
-- - non-stackables can be tracked as unique instances (durability, bind flags, etc.)

CREATE TABLE item_instance (
  id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id         BIGINT NOT NULL REFERENCES item(id) ON DELETE CASCADE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  bound_to_char   BIGINT REFERENCES character(id) ON DELETE SET NULL, -- if bound
  durability      INTEGER, -- optional if you model durability
  UNIQUE (id, item_id)
);
CREATE INDEX idx_item_instance_item ON item_instance(item_id);
CREATE INDEX idx_item_instance_bound ON item_instance(bound_to_char);

-- For stackables:
CREATE TABLE character_inventory (
  character_id  BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  item_id       BIGINT NOT NULL REFERENCES item(id) ON DELETE CASCADE,
  quantity      INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
  PRIMARY KEY (character_id, item_id)
);
CREATE INDEX idx_inventory_char ON character_inventory(character_id);
CREATE INDEX idx_inventory_item ON character_inventory(item_id);

-- Equipped unique items by slot (uses item_instance)
CREATE TABLE character_equipment (
  character_id       BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  equipment_slot_id  SMALLINT NOT NULL REFERENCES equipment_slot(id),
  item_instance_id   BIGINT UNIQUE REFERENCES item_instance(id) ON DELETE SET NULL,
  PRIMARY KEY (character_id, equipment_slot_id)
);
CREATE INDEX idx_equipment_char ON character_equipment(character_id);
CREATE INDEX idx_equipment_slot ON character_equipment(equipment_slot_id);

-- 6) Quests (with steps, requirements, progress, rewards)

CREATE TABLE quest (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title         TEXT NOT NULL,
  summary       TEXT,
  min_level     INTEGER NOT NULL DEFAULT 1 CHECK (min_level >= 1),
  repeatable    BOOLEAN NOT NULL DEFAULT FALSE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_quest_min_level ON quest(min_level);
CREATE TRIGGER trg_quest_uat BEFORE UPDATE ON quest
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Ordered steps for a quest
CREATE TABLE quest_step (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  quest_id      BIGINT NOT NULL REFERENCES quest(id) ON DELETE CASCADE,
  step_no       INTEGER NOT NULL CHECK (step_no >= 1),
  description   TEXT NOT NULL,
  UNIQUE (quest_id, step_no)
);
CREATE INDEX idx_qstep_quest ON quest_step(quest_id);

-- Requirements for a step (flexible: kill, collect, talk, reach_location...)
CREATE TABLE requirement_type (
  id    SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code  TEXT NOT NULL UNIQUE       -- kill, collect, talk, explore
);

CREATE TABLE quest_step_requirement (
  id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  quest_step_id       BIGINT NOT NULL REFERENCES quest_step(id) ON DELETE CASCADE,
  requirement_type_id SMALLINT NOT NULL REFERENCES requirement_type(id),
  target_ref          TEXT,               -- e.g., monster_code, npc_code, location_code, item_code
  target_count        INTEGER NOT NULL DEFAULT 1 CHECK (target_count >= 1)
);
CREATE INDEX idx_qsr_step ON quest_step_requirement(quest_step_id);

-- Character quest status & per-step progress
CREATE TABLE character_quest (
  character_id   BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  quest_id       BIGINT NOT NULL REFERENCES quest(id) ON DELETE CASCADE,
  accepted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  completed_at   TIMESTAMPTZ,
  PRIMARY KEY (character_id, quest_id)
);
CREATE INDEX idx_cq_char ON character_quest(character_id);
CREATE INDEX idx_cq_quest ON character_quest(quest_id);

CREATE TABLE character_quest_step_progress (
  character_id     BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  quest_step_id    BIGINT NOT NULL REFERENCES quest_step(id) ON DELETE CASCADE,
  progress_count   INTEGER NOT NULL DEFAULT 0 CHECK (progress_count >= 0),
  PRIMARY KEY (character_id, quest_step_id)
);
CREATE INDEX idx_cqsp_char ON character_quest_step_progress(character_id);
CREATE INDEX idx_cqsp_step ON character_quest_step_progress(quest_step_id);

-- Rewards (items/xp/gold); one quest can give multiple rewards
CREATE TABLE quest_reward (
  id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  quest_id     BIGINT NOT NULL REFERENCES quest(id) ON DELETE CASCADE,
  item_id      BIGINT REFERENCES item(id),
  item_qty     INTEGER CHECK (item_qty IS NULL OR item_qty >= 1),
  experience   BIGINT  CHECK (experience IS NULL OR experience >= 0),
  gold         BIGINT  CHECK (gold IS NULL OR gold >= 0),
  CHECK (item_id IS NOT NULL OR experience IS NOT NULL OR gold IS NOT NULL)
);
CREATE INDEX idx_qreward_quest ON quest_reward(quest_id);

-- 7) Guilds

CREATE TABLE guild (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name          TEXT NOT NULL UNIQUE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  leader_char_id BIGINT UNIQUE REFERENCES character(id) ON DELETE SET NULL -- optional enforced leader
);

CREATE TABLE guild_member (
  guild_id      BIGINT NOT NULL REFERENCES guild(id) ON DELETE CASCADE,
  character_id  BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  role_id       SMALLINT NOT NULL REFERENCES guild_role(id),
  joined_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (guild_id, character_id)
);
CREATE INDEX idx_gm_guild ON guild_member(guild_id);
CREATE INDEX idx_gm_char  ON guild_member(character_id);

-- 8) Achievements (and character unlocks)

CREATE TABLE achievement (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code          TEXT NOT NULL UNIQUE,
  title         TEXT NOT NULL,
  description   TEXT,
  points        INTEGER NOT NULL DEFAULT 10 CHECK (points >= 0),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE character_achievement (
  character_id  BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  achievement_id BIGINT NOT NULL REFERENCES achievement(id) ON DELETE CASCADE,
  unlocked_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (character_id, achievement_id)
);
CREATE INDEX idx_cach_char ON character_achievement(character_id);

-- 9) Leaderboards (snapshot-based to keep history)

CREATE TABLE leaderboard_snapshot (
  id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  leaderboard_type_id SMALLINT NOT NULL REFERENCES leaderboard_type(id),
  captured_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_lbs_type_time ON leaderboard_snapshot(leaderboard_type_id, captured_at);

CREATE TABLE leaderboard_entry (
  snapshot_id   BIGINT NOT NULL REFERENCES leaderboard_snapshot(id) ON DELETE CASCADE,
  rank          INTEGER NOT NULL CHECK (rank >= 1),
  character_id  BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
  score_value   BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (snapshot_id, rank),
  UNIQUE (snapshot_id, character_id)
);
CREATE INDEX idx_lbe_char ON leaderboard_entry(character_id);

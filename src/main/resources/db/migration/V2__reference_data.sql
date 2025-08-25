-- V2__reference_data.sql
-- Seed reference/lookup rows

INSERT INTO rarity (code, sort_order) VALUES
  ('common', 1), ('rare', 2), ('epic', 3), ('legendary', 4)
ON CONFLICT (code) DO NOTHING;

INSERT INTO item_type (code, sort_order) VALUES
  ('weapon', 10), ('armor', 20), ('consumable', 30), ('material', 40), ('quest_item', 50), ('trinket', 60)
ON CONFLICT (code) DO NOTHING;

INSERT INTO stat_type (code, unit) VALUES
  ('attack', 'points'),
  ('defense', 'points'),
  ('agility', 'points'),
  ('intellect', 'points'),
  ('stamina', 'points'),
  ('crit_chance', 'percent')
ON CONFLICT (code) DO NOTHING;

INSERT INTO equipment_slot (code, sort_order) VALUES
  ('head', 10), ('shoulders', 20), ('chest', 30), ('back', 40), ('hands', 50),
  ('belt', 60), ('legs', 70), ('feet', 80),
  ('ring', 90), ('amulet', 100),
  ('mainhand', 110), ('offhand', 120)
ON CONFLICT (code) DO NOTHING;

INSERT INTO class (code, description) VALUES
  ('warrior', 'Frontline melee fighter'),
  ('mage', 'Ranged spellcaster'),
  ('rogue', 'Stealthy melee'),
  ('ranger', 'Ranged physical'),
  ('cleric', 'Healer / support')
ON CONFLICT (code) DO NOTHING;

INSERT INTO race (code, description) VALUES
  ('human', 'Versatile'),
  ('elf', 'Graceful, long-lived'),
  ('orc', 'Strong and stoic'),
  ('dwarf', 'Sturdy craftspeople')
ON CONFLICT (code) DO NOTHING;

INSERT INTO guild_role (code, sort_order) VALUES
  ('leader', 1), ('officer', 2), ('member', 3)
ON CONFLICT (code) DO NOTHING;

INSERT INTO requirement_type (code) VALUES
  ('kill'), ('collect'), ('talk'), ('explore')
ON CONFLICT (code) DO NOTHING;

INSERT INTO leaderboard_type (code, description) VALUES
  ('level', 'Top characters by level'),
  ('achievements', 'Top by achievement points'),
  ('pvp_rating', 'Top by PvP rating'),
  ('wealth', 'Top by gold/wealth')
ON CONFLICT (code) DO NOTHING;

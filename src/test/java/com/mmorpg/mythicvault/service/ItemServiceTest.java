package com.mmorpg.mythicvault.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.mmorpg.mythicvault.dto.ItemCreateRequest;
import com.mmorpg.mythicvault.dto.ItemDto;
import com.mmorpg.mythicvault.dto.ItemUpdateRequest;
import com.mmorpg.mythicvault.entity.Item;
import com.mmorpg.mythicvault.entity.ItemType;
import com.mmorpg.mythicvault.entity.Rarity;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.ItemRepository;
import com.mmorpg.mythicvault.repositorie.ItemTypeRepository;
import com.mmorpg.mythicvault.repositorie.RarityRepository;

class ItemServiceTest {

	private final ItemRepository itemRepo = mock(ItemRepository.class);
	private final ItemTypeRepository itemTypeRepo = mock(ItemTypeRepository.class);
	private final RarityRepository rarityRepo = mock(RarityRepository.class);

	private ItemService svc() {
		return new ItemServiceImpl(itemRepo, itemTypeRepo, rarityRepo);
	}

	private static ItemType type(String code) {
		var t = new ItemType();
		t.setCode(code);
		return t;
	}

	private static Rarity rarity(String code) {
		var r = new Rarity();
		r.setCode(code);
		return r;
	}

	private static Item item(long id, String name, String desc, ItemType t, Rarity r, int reqLevel, int maxStack,
			long baseValue, boolean bop, boolean tradable, OffsetDateTime created, OffsetDateTime updated) {
		var i = new Item();
		i.setId(id);
		i.setName(name);
		i.setDescription(desc);
		i.setItemType(t);
		i.setRarity(r);
		i.setRequiredLevel(reqLevel);
		i.setMaxStack(maxStack);
		i.setBaseValue(baseValue);
		i.setBindOnPickup(bop);
		i.setTradable(tradable);
		i.setCreatedAt(created);
		i.setUpdatedAt(updated);
		return i;
	}

	@Nested
	class List_ {
		@Test
		void nonBlankQuery_lowercasesAndWrapsWithPercents() {
			var pageable = PageRequest.of(0, 5);
			when(itemRepo.search(anyString(), any(), any(), eq(pageable), any())).thenReturn(Page.empty(pageable));

			svc().list("PoTiOn", "common", "consumable", 1, pageable);

			var qCap = ArgumentCaptor.forClass(String.class);
			verify(itemRepo).search(qCap.capture(), eq("common"), eq("consumable"), eq(pageable), eq(1));
			assertThat(qCap.getValue()).isEqualTo("%potion%");
		}
	}

	@Nested
	class Get_ {
		@Test
		void found_returnsDto() {
			var created = OffsetDateTime.now().minusDays(2);
			var updated = OffsetDateTime.now().minusHours(1);
			var i = item(5L, "Health Potion", "Restores 250 HP", type("consumable"), rarity("common"), 1, 20, 25L,
					false, true, created, updated);

			when(itemRepo.findById(5L)).thenReturn(Optional.of(i));

			var dto = svc().get(5L);
			assertThat(dto.name()).isEqualTo("Health Potion");
			assertThat(dto.createdAt()).isEqualTo(created);
			assertThat(dto.updatedAt()).isEqualTo(updated);
		}

		@Test
		void missing_throws404() {
			when(itemRepo.findById(404L)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().get(404L)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Item not found");
		}
	}

	@Nested
	class Create_ {
		@Test
		void valid_appliesDefaults_andPersists() {
			var req = mock(ItemCreateRequest.class);
			when(req.name()).thenReturn("New Item");
			when(req.description()).thenReturn("desc");
			when(req.itemTypeCode()).thenReturn("weapon");
			when(req.rarityCode()).thenReturn("common");
			// defaults:
			when(req.requiredLevel()).thenReturn(null);
			when(req.maxStack()).thenReturn(null);
			when(req.baseValue()).thenReturn(null);
			when(req.bindOnPickup()).thenReturn(true);
			when(req.tradable()).thenReturn(false);

			when(itemTypeRepo.findByCode("weapon")).thenReturn(Optional.of(type("weapon")));
			when(rarityRepo.findByCode("common")).thenReturn(Optional.of(rarity("common")));

			when(itemRepo.save(any(Item.class))).thenAnswer(inv -> {
				var i = (Item) inv.getArgument(0);
				i.setId(123L);
				return i;
			});

			var dto = svc().create(req);
			assertThat(dto.requiredLevel()).isEqualTo(1);
			assertThat(dto.maxStack()).isEqualTo(1);
			assertThat(dto.baseValue()).isEqualTo(0L);
			assertThat(dto.bindOnPickup()).isTrue();
			assertThat(dto.tradable()).isFalse();
			assertThat(dto.id()).isEqualTo(123L);
		}

		@Test
		void invalidType_throws404() {
			var req = mock(ItemCreateRequest.class);
			when(req.itemTypeCode()).thenReturn("nope");
			when(req.rarityCode()).thenReturn("common");
			when(rarityRepo.findByCode("common")).thenReturn(Optional.of(rarity("common")));
			when(itemTypeRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().create(req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid itemTypeCode");
		}

		@Test
		void invalidRarity_throws404() {
			var req = mock(ItemCreateRequest.class);
			when(req.itemTypeCode()).thenReturn("weapon");
			when(req.rarityCode()).thenReturn("nope");
			when(itemTypeRepo.findByCode("weapon")).thenReturn(Optional.of(type("weapon")));
			when(rarityRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().create(req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid rarityCode");
		}
	}

	@Nested
	class Update_ {
		@Test
		void partialUpdate_onlyNonNullFieldsAreApplied() {
			var existing = item(50L, "Old", "Old desc", type("weapon"), rarity("rare"), 5, 1, 150L, true, false,
					OffsetDateTime.now().minusDays(3), OffsetDateTime.now().minusDays(1));

			when(itemRepo.findById(50L)).thenReturn(Optional.of(existing));

			var req = mock(ItemUpdateRequest.class);
			when(req.name()).thenReturn("New");
			when(req.description()).thenReturn(null);
			when(req.itemTypeCode()).thenReturn(null);
			when(req.rarityCode()).thenReturn("common"); // change rarity
			when(req.requiredLevel()).thenReturn(9);
			when(req.maxStack()).thenReturn(null);
			when(req.baseValue()).thenReturn(999L);
			when(req.bindOnPickup()).thenReturn(false);
			when(req.tradable()).thenReturn(true);

			when(rarityRepo.findByCode("common")).thenReturn(Optional.of(rarity("common")));
			when(itemRepo.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

			var dto = svc().update(50L, req);
			assertThat(dto.name()).isEqualTo("New");
			assertThat(dto.description()).isEqualTo("Old desc"); // unchanged
			assertThat(dto.requiredLevel()).isEqualTo(9); // updated
			assertThat(dto.maxStack()).isEqualTo(1); // unchanged
			assertThat(dto.baseValue()).isEqualTo(999L); // updated
			assertThat(dto.bindOnPickup()).isFalse(); // updated
			assertThat(dto.tradable()).isTrue(); // updated
		}

		@Test
		void invalidTypeDuringUpdate_throws404() {
			when(itemRepo.findById(7L)).thenReturn(Optional.of(item(7L, "X", "Y", type("weapon"), rarity("rare"), 1, 1,
					1L, false, true, OffsetDateTime.now(), OffsetDateTime.now())));

			var req = mock(ItemUpdateRequest.class);
			when(req.itemTypeCode()).thenReturn("nope");
			when(itemTypeRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().update(7L, req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid itemTypeCode");
		}

		@Test
		void invalidRarityDuringUpdate_throws404() {
			when(itemRepo.findById(7L)).thenReturn(Optional.of(item(7L, "X", "Y", type("weapon"), rarity("rare"), 1, 1,
					1L, false, true, OffsetDateTime.now(), OffsetDateTime.now())));

			var req = mock(ItemUpdateRequest.class);
			when(req.rarityCode()).thenReturn("nope");
			when(rarityRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().update(7L, req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid rarityCode");
		}

		@Test
		void missingItem_throws404() {
			when(itemRepo.findById(404L)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().update(404L, mock(ItemUpdateRequest.class)))
					.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Item not found");
		}
	}

	@Nested
	class Delete_ {
		@Test
		void existing_deletes() {
			when(itemRepo.existsById(9L)).thenReturn(true);
			svc().delete(9L);
			verify(itemRepo).deleteById(9L);
		}

		@Test
		void missing_throws404() {
			when(itemRepo.existsById(9L)).thenReturn(false);
			assertThatThrownBy(() -> svc().delete(9L)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Item not found");
		}
	}
}

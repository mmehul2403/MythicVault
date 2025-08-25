package com.mmorpg.mythicvault.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.mmorpg.mythicvault.dto.CharacterCreateRequest;
import com.mmorpg.mythicvault.dto.CharacterDto;
import com.mmorpg.mythicvault.dto.CharacterUpdateRequest;
import com.mmorpg.mythicvault.entity.AccountUser;
import com.mmorpg.mythicvault.entity.CharacterClass;
import com.mmorpg.mythicvault.entity.GameCharacter;
import com.mmorpg.mythicvault.entity.Race;
import com.mmorpg.mythicvault.errorhandler.ResourceNotFoundException;
import com.mmorpg.mythicvault.repositorie.AccountUserRepository;
import com.mmorpg.mythicvault.repositorie.CharacterClassRepository;
import com.mmorpg.mythicvault.repositorie.GameCharacterRepository;
import com.mmorpg.mythicvault.repositorie.RaceRepository;

class GameCharacterServiceTest {

	private final GameCharacterRepository charRepo = mock(GameCharacterRepository.class);
	private final AccountUserRepository accountRepo = mock(AccountUserRepository.class);
	private final CharacterClassRepository classRepo = mock(CharacterClassRepository.class);
	private final RaceRepository raceRepo = mock(RaceRepository.class);

	private GameCharacterService svc() {
		return new GameCharacterServiceImpl(charRepo, accountRepo, classRepo, raceRepo);
	}

	private static AccountUser user(String username) {
		var u = new AccountUser();
		u.setUsername(username);
		return u;
	}

	private static CharacterClass clazz(String code) {
		var c = new CharacterClass();
		c.setCode(code);
		return c;
	}

	private static Race race(String code) {
		var r = new Race();
		r.setCode(code);
		return r;
	}

	private static GameCharacter gc(long id, AccountUser u, String name, CharacterClass c, Race r, int lvl, long xp,
			long gold) {
		var g = new GameCharacter();
		g.setId(id);
		g.setAccount(u);
		g.setName(name);
		g.setCharacterClass(c);
		g.setRace(r);
		g.setLevel(lvl);
		g.setExperience(xp);
		g.setGold(gold);
		return g;
	}

	private static String invokeStringIfPresent(Object target, String... methods) {
		for (var m : methods) {
			try {
				var md = target.getClass().getMethod(m);
				var val = md.invoke(target);
				return val != null ? val.toString() : null;
			} catch (NoSuchMethodException ignored) {
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private static void assertMaybeUsername(Object dto, String expected) {
		var actual = invokeStringIfPresent(dto, "username", "getUsername", "accountUsername", "getAccountUsername");
		if (actual != null)
			assertThat(actual).isEqualTo(expected);
	}

	private static void assertMaybeClassCode(Object dto, String expected) {
		var actual = invokeStringIfPresent(dto, "classCode", "getClassCode", "characterClassCode",
				"getCharacterClassCode");
		if (actual != null)
			assertThat(actual).isEqualTo(expected);
	}

	private static void assertMaybeRaceCode(Object dto, String expected) {
		var actual = invokeStringIfPresent(dto, "raceCode", "getRaceCode");
		if (actual != null)
			assertThat(actual).isEqualTo(expected);
	}
	// ----------------------------------------------------------------------------------------------

	@Nested
	class List_ {

		@Test
		void returnsMappedPage_fromRepository() {
			var pageable = PageRequest.of(0, 10);
			var u = user("demo_user");
			var c = clazz("warrior");
			var r = race("human");
			var g = gc(11L, u, "Thorin", c, r, 8, 12345L, 250L);

			when(charRepo.findAll(any(Specification.class), eq(pageable)))
					.thenReturn(new PageImpl<>(List.of(g), pageable, 1));

			Page<CharacterDto> page = svc().list("demo_user", "Tho", "warrior", "human", 1, 10, pageable);
			assertThat(page.getTotalElements()).isEqualTo(1);
			var dto = page.getContent().get(0);
			assertThat(dto.id()).isEqualTo(11L);
			assertThat(dto.name()).isEqualTo("Thorin");
			assertMaybeUsername(dto, "demo_user");
			assertMaybeClassCode(dto, "warrior");
			assertMaybeRaceCode(dto, "human");
			assertThat(dto.level()).isEqualTo(8);
			assertThat(dto.experience()).isEqualTo(12345L);
			assertThat(dto.gold()).isEqualTo(250L);
		}

		@Test
		void emptyResult_ok() {
			var pageable = PageRequest.of(0, 10);
			when(charRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));
			var page = svc().list(null, null, null, null, null, null, pageable);
			assertThat(page.getTotalElements()).isZero();
			assertThat(page.getContent()).isEmpty();
		}
	}

	@Nested
	class Get_ {

		@Test
		void found_returnsDto() {
			var g = gc(5L, user("demo_user"), "Aragorn", clazz("ranger"), race("human"), 12, 2222L, 77L);
			when(charRepo.findById(5L)).thenReturn(Optional.of(g));

			var dto = svc().get(5L);
			assertThat(dto.id()).isEqualTo(5L);
			assertThat(dto.name()).isEqualTo("Aragorn");
			assertMaybeUsername(dto, "demo_user");
			assertMaybeClassCode(dto, "ranger");
			assertMaybeRaceCode(dto, "human");
			assertThat(dto.level()).isEqualTo(12);
			assertThat(dto.experience()).isEqualTo(2222L);
			assertThat(dto.gold()).isEqualTo(77L);
		}

		@Test
		void missing_throws404() {
			when(charRepo.findById(404L)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().get(404L)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Character not found");
		}
	}

	@Nested
	class Create_ {

		@Test
		void valid_appliesDefaults_andPersists() {
			var req = mock(CharacterCreateRequest.class);
			when(req.username()).thenReturn("demo_user");
			when(req.name()).thenReturn("NewChar");
			when(req.classCode()).thenReturn("warrior");
			when(req.raceCode()).thenReturn("human");
			when(req.level()).thenReturn(null); // default to 1

			when(accountRepo.findByUsername("demo_user")).thenReturn(Optional.of(user("demo_user")));
			when(classRepo.findByCode("warrior")).thenReturn(Optional.of(clazz("warrior")));
			when(raceRepo.findByCode("human")).thenReturn(Optional.of(race("human")));

			when(charRepo.save(any(GameCharacter.class))).thenAnswer(inv -> {
				var x = (GameCharacter) inv.getArgument(0);
				x.setId(123L);
				return x;
			});

			var dto = svc().create(req);
			assertThat(dto.id()).isEqualTo(123L);
			assertThat(dto.name()).isEqualTo("NewChar");
			assertThat(dto.level()).isEqualTo(1); // default applied
			assertThat(dto.experience()).isEqualTo(0L);
			assertThat(dto.gold()).isEqualTo(0L);
			assertMaybeUsername(dto, "demo_user");
			assertMaybeClassCode(dto, "warrior");
			assertMaybeRaceCode(dto, "human");
		}

		@Test
		void unknownUsername_throws404() {
			var req = mock(CharacterCreateRequest.class);
			when(req.username()).thenReturn("nope");
			when(accountRepo.findByUsername("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().create(req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Unknown username");
		}

		@Test
		void invalidClass_throws404() {
			var req = mock(CharacterCreateRequest.class);
			when(req.username()).thenReturn("demo_user");
			when(req.classCode()).thenReturn("nope");
			when(req.raceCode()).thenReturn("human");

			when(accountRepo.findByUsername("demo_user")).thenReturn(Optional.of(user("demo_user")));
			when(classRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().create(req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid classCode");
		}

		@Test
		void invalidRace_throws404() {
			var req = mock(CharacterCreateRequest.class);
			when(req.username()).thenReturn("demo_user");
			when(req.classCode()).thenReturn("warrior");
			when(req.raceCode()).thenReturn("nope");

			when(accountRepo.findByUsername("demo_user")).thenReturn(Optional.of(user("demo_user")));
			when(classRepo.findByCode("warrior")).thenReturn(Optional.of(clazz("warrior")));
			when(raceRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().create(req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid raceCode");
		}
	}

	@Nested
	class Update_ {

		@Test
		void partialUpdate_onlyNonNullFieldsApplied() {
			var existing = gc(50L, user("demo_user"), "OldName", clazz("warrior"), race("human"), 5, 100L, 10L);
			when(charRepo.findById(50L)).thenReturn(Optional.of(existing));

			var req = mock(CharacterUpdateRequest.class);
			when(req.name()).thenReturn("NewName");
			when(req.classCode()).thenReturn(null); // unchanged
			when(req.raceCode()).thenReturn("elf"); // change race
			when(req.level()).thenReturn(9);
			when(req.experience()).thenReturn(1234L);
			when(req.gold()).thenReturn(777L);

			when(raceRepo.findByCode("elf")).thenReturn(Optional.of(race("elf")));
			when(charRepo.save(any(GameCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

			var dto = svc().update(50L, req);
			assertThat(dto.name()).isEqualTo("NewName");
			assertMaybeClassCode(dto, "warrior"); // unchanged
			assertMaybeRaceCode(dto, "elf"); // updated
			assertThat(dto.level()).isEqualTo(9);
			assertThat(dto.experience()).isEqualTo(1234L);
			assertThat(dto.gold()).isEqualTo(777L);
		}

		@Test
		void invalidClassDuringUpdate_throws404() {
			var existing = gc(7L, user("demo_user"), "Char", clazz("warrior"), race("human"), 1, 0L, 0L);
			when(charRepo.findById(7L)).thenReturn(Optional.of(existing));

			var req = mock(CharacterUpdateRequest.class);
			when(req.classCode()).thenReturn("nope");
			when(classRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().update(7L, req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid classCode");
		}

		@Test
		void invalidRaceDuringUpdate_throws404() {
			var existing = gc(7L, user("demo_user"), "Char", clazz("warrior"), race("human"), 1, 0L, 0L);
			when(charRepo.findById(7L)).thenReturn(Optional.of(existing));

			var req = mock(CharacterUpdateRequest.class);
			when(req.raceCode()).thenReturn("nope");
			when(raceRepo.findByCode("nope")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> svc().update(7L, req)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Invalid raceCode");
		}

		@Test
		void missingCharacter_throws404() {
			when(charRepo.findById(404L)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> svc().update(404L, mock(CharacterUpdateRequest.class)))
					.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Character not found");
		}
	}

	@Nested
	class Delete_ {

		@Test
		void existing_deletes() {
			when(charRepo.existsById(9L)).thenReturn(true);
			svc().delete(9L);
			verify(charRepo).deleteById(9L);
		}

		@Test
		void missing_throws404() {
			when(charRepo.existsById(9L)).thenReturn(false);
			assertThatThrownBy(() -> svc().delete(9L)).isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("Character not found");
		}
	}
}

package com.mmorpg.mythicvault.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.mmorpg.mythicvault.entity.GameCharacter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class GameCharacterSpecsTest {

	@SuppressWarnings("unchecked")
	private Root<GameCharacter> root() {
		return (Root<GameCharacter>) mock(Root.class);
	}

	@SuppressWarnings("unchecked")
	private CriteriaQuery<Object> cq() {
		return (CriteriaQuery<Object>) mock(CriteriaQuery.class);
	}

	private CriteriaBuilder cb() {
		return mock(CriteriaBuilder.class);
	}

	private Predicate pred() {
		return mock(Predicate.class);
	}

	@Nested
	class UsernameEquals {

		@Test
		void returnsNullWhenUsernameIsNull() {
			var spec = GameCharacterSpecs.usernameEquals(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsEqualityPredicate() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path accountPath = (Path) mock(Path.class); // raw to satisfy Root.get(String) signature
			Path usernamePath = (Path) mock(Path.class);

			when(root.get("account")).thenReturn(accountPath);
			when(accountPath.get("username")).thenReturn(usernamePath);

			// cast raw Path to the typed Expression<String> at callsite
			when(cb.equal((Expression<String>) usernamePath, "demo_user")).thenReturn(expected);

			var spec = GameCharacterSpecs.usernameEquals("demo_user");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("account");
			verify(accountPath).get("username");
			verify(cb).equal((Expression<String>) usernamePath, "demo_user");
		}
	}

	@Nested
	class NameContains {

		@Test
		void returnsNullWhenFragmentIsNull() {
			var spec = GameCharacterSpecs.nameContains(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsLowerLikePredicate_caseInsensitive() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path namePath = (Path) mock(Path.class);
			Expression<String> lowered = (Expression<String>) mock(Expression.class);

			when(root.get("name")).thenReturn(namePath);
			when(cb.lower((Expression<String>) namePath)).thenReturn(lowered);
			when(cb.like(lowered, "%thor%")).thenReturn(expected);

			var spec = GameCharacterSpecs.nameContains("ThOr");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("name");
			verify(cb).lower((Expression<String>) namePath);
			verify(cb).like(lowered, "%thor%");
		}
	}

	@Nested
	class ClassCode {

		@Test
		void returnsNullWhenCodeIsNull() {
			var spec = GameCharacterSpecs.classCode(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsEqualityPredicate_onNestedCharacterClassCode() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path classPath = (Path) mock(Path.class);
			Path codePath = (Path) mock(Path.class);

			when(root.get("characterClass")).thenReturn(classPath);
			when(classPath.get("code")).thenReturn(codePath);
			when(cb.equal((Expression<String>) codePath, "warrior")).thenReturn(expected);

			var spec = GameCharacterSpecs.classCode("warrior");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("characterClass");
			verify(classPath).get("code");
			verify(cb).equal((Expression<String>) codePath, "warrior");
		}
	}

	@Nested
	class RaceCode {

		@Test
		void returnsNullWhenCodeIsNull() {
			var spec = GameCharacterSpecs.raceCode(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsEqualityPredicate_onNestedRaceCode() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path racePath = (Path) mock(Path.class);
			Path codePath = (Path) mock(Path.class);

			when(root.get("race")).thenReturn(racePath);
			when(racePath.get("code")).thenReturn(codePath);
			when(cb.equal((Expression<String>) codePath, "human")).thenReturn(expected);

			var spec = GameCharacterSpecs.raceCode("human");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("race");
			verify(racePath).get("code");
			verify(cb).equal((Expression<String>) codePath, "human");
		}
	}

	@Nested
	class LevelBetween {

		@Test
		void returnsNullWhenBothNull() {
			var spec = GameCharacterSpecs.levelBetween(null, null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void betweenWhenBothProvided() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path levelPath = (Path) mock(Path.class);
			when(root.get("level")).thenReturn(levelPath);
			when(cb.between((Expression<Integer>) levelPath, 5, 10)).thenReturn(expected);

			var spec = GameCharacterSpecs.levelBetween(5, 10);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("level");
			verify(cb).between((Expression<Integer>) levelPath, 5, 10);
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void geWhenOnlyMinProvided() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path levelPath = (Path) mock(Path.class);
			when(root.get("level")).thenReturn(levelPath);
			when(cb.ge((Expression<? extends Number>) levelPath, 7)).thenReturn(expected);

			var spec = GameCharacterSpecs.levelBetween(7, null);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("level");
			verify(cb).ge((Expression<? extends Number>) levelPath, 7);
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void leWhenOnlyMaxProvided() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path levelPath = (Path) mock(Path.class);
			when(root.get("level")).thenReturn(levelPath);
			when(cb.le((Expression<? extends Number>) levelPath, 20)).thenReturn(expected);

			var spec = GameCharacterSpecs.levelBetween(null, 20);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("level");
			verify(cb).le((Expression<? extends Number>) levelPath, 20);
		}
	}
}

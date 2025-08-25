package com.mmorpg.mythicvault.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.mmorpg.mythicvault.entity.Item;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class ItemSpecsTest {

	@SuppressWarnings("unchecked")
	private Root<Item> root() {
		return (Root<Item>) mock(Root.class);
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
	class NameContains_ {

		@Test
		void returnsNullWhenQIsNull() {
			var spec = ItemSpecs.nameContains(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsLowerLike_caseInsensitive() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path namePath = (Path) mock(Path.class);
			Expression<String> lowered = (Expression<String>) mock(Expression.class);

			when(root.get("name")).thenReturn(namePath);
			when(cb.lower((Expression<String>) namePath)).thenReturn(lowered);
			when(cb.like(lowered, "%potion%")).thenReturn(expected);

			var spec = ItemSpecs.nameContains("PoTiOn");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("name");
			verify(cb).lower((Expression<String>) namePath);
			verify(cb).like(lowered, "%potion%");
		}
	}

	@Nested
	class Rarity_ {

		@Test
		void returnsNullWhenCodeIsNull() {
			var spec = ItemSpecs.rarity(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsEqualityPredicate_onNestedRarityCode() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path rarityPath = (Path) mock(Path.class);
			Path codePath = (Path) mock(Path.class);

			when(root.get("rarity")).thenReturn(rarityPath);
			when(rarityPath.get("code")).thenReturn(codePath);
			when(cb.equal((Expression<String>) codePath, "rare")).thenReturn(expected);

			var spec = ItemSpecs.rarity("rare");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("rarity");
			verify(rarityPath).get("code");
			verify(cb).equal((Expression<String>) codePath, "rare");
		}
	}

	@Nested
	class Type_ {

		@Test
		void returnsNullWhenCodeIsNull() {
			var spec = ItemSpecs.type(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsEqualityPredicate_onNestedItemTypeCode() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path typePath = (Path) mock(Path.class);
			Path codePath = (Path) mock(Path.class);

			when(root.get("itemType")).thenReturn(typePath);
			when(typePath.get("code")).thenReturn(codePath);
			when(cb.equal((Expression<String>) codePath, "weapon")).thenReturn(expected);

			var spec = ItemSpecs.type("weapon");
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("itemType");
			verify(typePath).get("code");
			verify(cb).equal((Expression<String>) codePath, "weapon");
		}
	}

	@Nested
	class UsableByLevel_ {

		@Test
		void returnsNullWhenLevelIsNull() {
			var spec = ItemSpecs.usableByLevel(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsLePredicate_onRequiredLevel() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path levelPath = (Path) mock(Path.class);

			when(root.get("requiredLevel")).thenReturn(levelPath);
			when(cb.le((Expression<? extends Number>) levelPath, 10)).thenReturn(expected);

			var spec = ItemSpecs.usableByLevel(10);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("requiredLevel");
			verify(cb).le((Expression<? extends Number>) levelPath, 10);
		}
	}
}

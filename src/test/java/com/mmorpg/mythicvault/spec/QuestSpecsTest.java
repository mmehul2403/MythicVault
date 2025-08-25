package com.mmorpg.mythicvault.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.mmorpg.mythicvault.entity.Quest;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class QuestSpecsTest {

	@SuppressWarnings("unchecked")
	private Root<Quest> root() {
		return (Root<Quest>) mock(Root.class);
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
	class MinLevelLessOrEqual_ {

		@Test
		void returnsNullWhenLevelIsNull() {
			var spec = QuestSpecs.minLevelLessOrEqual(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void buildsLePredicate_onMinLevel() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path minLevelPath = (Path) mock(Path.class);

			when(root.get("minLevel")).thenReturn(minLevelPath);
			// cast Path -> Expression<? extends Number> to match CriteriaBuilder.le
			// signature
			when(cb.le((Expression<? extends Number>) minLevelPath, 15)).thenReturn(expected);

			var spec = QuestSpecs.minLevelLessOrEqual(15);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("minLevel");
			verify(cb).le((Expression<? extends Number>) minLevelPath, 15);
		}
	}

	@Nested
	class Repeatable_ {

		@Test
		void returnsNullWhenRepeatableIsNull() {
			var spec = QuestSpecs.repeatable(null);
			var p = spec.toPredicate(root(), cq(), cb());
			assertThat(p).isNull();
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void true_buildsIsTruePredicate() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path repeatablePath = (Path) mock(Path.class);
			when(root.get("repeatable")).thenReturn(repeatablePath);
			when(cb.isTrue((Expression<Boolean>) repeatablePath)).thenReturn(expected);

			var spec = QuestSpecs.repeatable(true);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("repeatable");
			verify(cb).isTrue((Expression<Boolean>) repeatablePath);
		}

		@Test
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void false_buildsIsFalsePredicate() {
			var root = root();
			var query = cq();
			var cb = cb();
			var expected = pred();

			Path repeatablePath = (Path) mock(Path.class);
			when(root.get("repeatable")).thenReturn(repeatablePath);
			when(cb.isFalse((Expression<Boolean>) repeatablePath)).thenReturn(expected);

			var spec = QuestSpecs.repeatable(false);
			var actual = spec.toPredicate(root, query, cb);

			assertThat(actual).isSameAs(expected);
			verify(root).get("repeatable");
			verify(cb).isFalse((Expression<Boolean>) repeatablePath);
		}
	}
}

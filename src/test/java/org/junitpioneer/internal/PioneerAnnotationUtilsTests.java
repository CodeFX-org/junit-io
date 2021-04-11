/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.AnnotationCheck;
import static org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.Child;
import static org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.NonRepeatableTestAnnotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.AnnotationCluster;
import org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.Enclosing;
import org.junitpioneer.internal.PioneerAnnotationUtilsTestCases.RepeatableTestAnnotation;

@DisplayName("Pioneer Annotation utilities")
public class PioneerAnnotationUtilsTests {

	@Nested
	@DisplayName("checking if any non-repeated annotation is present")
	class AnyAnnotationPresentTests {

		@Test
		@DisplayName("does not find annotations if they are not present")
		void doesNotFindNotPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyAnnotationPresent(testContext, PioneerAnnotationUtilsTestCases.NotInheritedAnnotation.class);

			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("finds directly present annotations")
		void directlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("direct"));

			boolean result = PioneerAnnotationUtils
					.isAnyAnnotationPresent(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds indirectly present annotations")
		void indirectlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Child.class,
				Child.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyAnnotationPresent(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds meta-present annotations")
		void metaPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("meta"));

			boolean result = PioneerAnnotationUtils
					.isAnyAnnotationPresent(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds enclosing present annotations")
		void enclosingPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Enclosing.class,
				Enclosing.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyAnnotationPresent(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

	}

	@Nested
	@DisplayName("checking if any repeated annotation is present")
	class AnyRepeatedAnnotationPresentTests {

		@Test
		@DisplayName("does not find annotations if they are not present")
		void doesNotFindNotPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyRepeatableAnnotationPresent(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("finds directly present annotations")
		void directlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("direct"));

			boolean result = PioneerAnnotationUtils
					.isAnyRepeatableAnnotationPresent(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds indirectly present annotations")
		void indirectlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Child.class,
				Child.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyRepeatableAnnotationPresent(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds meta-present annotations")
		void metaPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("meta"));

			boolean result = PioneerAnnotationUtils
					.isAnyRepeatableAnnotationPresent(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("finds enclosing present annotations")
		void enclosingPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Enclosing.class,
				Enclosing.class.getMethod("notAnnotated"));

			boolean result = PioneerAnnotationUtils
					.isAnyRepeatableAnnotationPresent(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isTrue();
		}

	}

	@Nested
	@DisplayName("finding closest non-repeatable enclosing annotation")
	class ClosestAnnotation {

		@Test
		@DisplayName("does not find not present annotations")
		void notPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("notAnnotated"));

			Optional<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingAnnotation(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isNotPresent();
		}

		@Test
		@DisplayName("finds directly present even if all other types are present")
		void directlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("direct"));

			Optional<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingAnnotation(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isPresent();
			assertThat(result.get().value()).contains("directly present");
		}

		@Test
		@DisplayName("finds meta-present if no directly present annotation was found")
		void metaPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("meta"));

			Optional<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingAnnotation(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isPresent();
			assertThat(result.get().value()).contains("meta present");
		}

		@Test
		@DisplayName("finds enclosing present if no directly present or meta annotation was found")
		void enclosingPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("notAnnotated"));

			Optional<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingAnnotation(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isPresent();
			assertThat(result.get().value()).contains("enclosing present");
		}

		@Test
		@DisplayName("finds indirectly present if no directly present, meta present or enclosing present annotation was found")
		void indirectlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Child.class,
				Child.class.getMethod("notAnnotated"));

			Optional<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingAnnotation(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isPresent();
			assertThat(result.get().value()).contains("indirectly present");
		}

	}

	@Nested
	@DisplayName("finding closest repeatable enclosing annotation")
	class ClosestRepeatableAnnotation {

		@Test
		@DisplayName("does not find not present annotations")
		void notPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("notAnnotated"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("finds directly present even if all other types are present")
		void directlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("direct"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).hasSize(2).allMatch(annotation -> annotation.value().contains("directly present"));
		}

		@Test
		@DisplayName("finds meta-present if no directly present annotation was found")
		void metaPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("meta"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).hasSize(1).allMatch(annotation -> annotation.value().contains("meta present"));
		}

		@Test
		@DisplayName("finds enclosing present if no directly present or meta annotation was found")
		void enclosingPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCluster.class,
				AnnotationCluster.class.getMethod("notAnnotated"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).hasSize(2).allMatch(annotation -> annotation.value().contains("enclosing present"));
		}

		@Test
		@DisplayName("finds indirectly present if no directly present, meta present or enclosing present annotation was found")
		void indirectlyPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(Child.class,
				Child.class.getMethod("notAnnotated"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findClosestEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).hasSize(1).allMatch(annotation -> annotation.value().contains("indirectly present"));
		}

	}

	@Nested
	@DisplayName("getting all non-repeatable annotations")
	class FindingAllAnnotations {

		@Test
		@DisplayName("does not find not present annotations")
		void notPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("notAnnotated"));

			Stream<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findAllEnclosingAnnotations(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("finds all enclosing annotations")
		void allEnclosing() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(
				AnnotationCluster.NestedClass.NestedNestedClass.class,
				AnnotationCluster.NestedClass.NestedNestedClass.class.getMethod("annotated"));

			Stream<NonRepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findAllEnclosingAnnotations(testContext, NonRepeatableTestAnnotation.class);

			assertThat(result)
					.hasSize(5)
					.extracting(NonRepeatableTestAnnotation::value)
					.containsExactlyInAnyOrder(
						"This annotation is indirectly present (inherited) on any method of an implementing class.",
						"This annotation is enclosing present", "Nested 1", "Nested 2", "Nested 3");
		}

	}

	@Nested
	@DisplayName("getting all repeatable annotations")
	class FindingAllRepeatableAnnotations {

		@Test
		@DisplayName("does not find not present annotations")
		void notPresent() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(AnnotationCheck.class,
				AnnotationCheck.class.getMethod("notAnnotated"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findAllEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("finds all enclosing annotations")
		void allEnclosing() throws NoSuchMethodException {
			TestExtensionContext testContext = new TestExtensionContext(
				AnnotationCluster.NestedClass.NestedNestedClass.class,
				AnnotationCluster.NestedClass.NestedNestedClass.class.getMethod("annotated"));

			Stream<RepeatableTestAnnotation> result = PioneerAnnotationUtils
					.findAllEnclosingRepeatableAnnotations(testContext, RepeatableTestAnnotation.class);

			assertThat(result)
					.hasSize(8)
					.extracting(RepeatableTestAnnotation::value)
					.containsExactlyInAnyOrder(
						"This annotation is indirectly present (inherited) on any method of an implementing class.",
						"This annotation is enclosing present", "This annotation is also enclosing present",
						"Repeatable nested 1", "Repeatable nested 2", "Repeatable nested 3", "Repeatable nested 4",
						"Repeatable nested 5");
		}

	}

	@Nested
	@DisplayName("getting annotated annotations")
	class AnnotatedAnnotations {

		@Test
		@DisplayName("finds annotated annotations on a method")
		void onMethods() throws NoSuchMethodException {
			Method method = PioneerAnnotationUtilsTestCases.AnnotatedAnnotations.class.getMethod("annotated");

			List<Annotation> result = PioneerAnnotationUtils.findAnnotatedAnnotations(method, Inherited.class);

			assertThat(result)
					.hasSize(3)
					.allSatisfy(annotation -> assertThat(annotation)
							.isInstanceOfAny(RepeatableTestAnnotation.class, NonRepeatableTestAnnotation.class))
					.extractingResultOf("value")
					.containsExactlyInAnyOrder("Inherited 4", "Inherited 5", "Inherited 6");
		}

		@Test
		@DisplayName("finds annotated annotations on a class")
		void onClass() {
			List<Annotation> result = PioneerAnnotationUtils
					.findAnnotatedAnnotations(PioneerAnnotationUtilsTestCases.AnnotatedAnnotations.class,
						Inherited.class);

			assertThat(result)
					.hasSize(3)
					.allSatisfy(annotation -> assertThat(annotation)
							.isInstanceOfAny(RepeatableTestAnnotation.class, NonRepeatableTestAnnotation.class))
					.extractingResultOf("value")
					.containsExactlyInAnyOrder("Inherited 1", "Inherited 2", "Inherited 3");
		}

	}

}

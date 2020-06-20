/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.params;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

class DisabledIfNameExtensionTests {

	@Nested
	class SubstringTests {

		@Test
		void single_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(SubstringTestCases.class, "single", "java.lang.String");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(1);
			assertThat(results.numberOfSkippedTests()).isEqualTo(5);
		}

		@Test
		void multiple_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(SubstringTestCases.class, "multiple", "int");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(3);
			assertThat(results.numberOfSkippedTests()).isEqualTo(2);
		}

		@Test
		void methodNameContainsSubstring_containerNotSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(SubstringTestCases.class, "methodNameContains", "int");

			assertThat(results.numberOfStartedTests()).isEqualTo(3);
		}

	}

	@Nested
	class RegExpTests {

		@Test
		void single_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(RegExpTestCases.class, "single", "java.lang.String");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(2);
			assertThat(results.numberOfSkippedTests()).isEqualTo(4);
		}

		@Test
		void multiple_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(RegExpTestCases.class, "multiple", "int");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(2);
			assertThat(results.numberOfSkippedTests()).isEqualTo(3);
		}

		@Test
		void methodNameMatchesRegExp_containerNotSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(RegExpTestCases.class, "methodNameMatches", "int");

			assertThat(results.numberOfStartedTests()).isEqualTo(3);
		}

	}

	// TEST CASES -------------------------------------------------------------------

	static class SubstringTestCases {

		//@formatter:off
		@DisableIfDisplayName(contains = "disable")
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(
				strings = {
						"disable who",
						"you, disable you",
						"why am I disabled",
						"what has been disabled must stay disabled",
						"fine disable me all you want",
						"not those one, though!"
				}
		)
		//@formatter:on
		void single(String reason) {
			if (reason.contains("disable"))
				fail("Test should've been disabled " + reason);
		}

		@DisableIfDisplayName(contains = { "1", "2" })
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 2, 3, 4, 5 })
		void multiple(int number) {
			if (number == 1 || number == 2)
				fail("Test should've been disabled for " + number);
		}

		@DisableIfDisplayName(contains = "Contains")
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 2, 3 })
		void methodNameContains(int unusedParameter) {
		}

	}

	static class RegExpTestCases {

		//@formatter:off
		@DisableIfDisplayName(matches = ".*disabled?\\s.*")
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(
				strings = {
						"disable who",
						"you, disable you",
						"why am I disabled",
						"what has been disabled must stay disabled",
						"fine disable me all you want",
						"not those one, though!"
				}
		)
		void single(String reason) {
			boolean shouldBeDisabled = Arrays
					.asList(
							"you, disable you",
							"what has been disabled must stay disabled",
							"fine disable me all you want")
					.contains(reason);
			if (shouldBeDisabled)
				fail("Test should've been disabled " + reason);
		}
		//@formatter:on

		@DisableIfDisplayName(matches = { ".*10[^0]*", ".*10{3,4}[^0]*" })
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 10, 100, 1_000, 10_000 })
		void multiple(int number) {
			if (number == 10 || number == 1_000 || number == 10_000)
				fail("Test should've been disabled for " + number);
		}

		@DisableIfDisplayName(matches = ".*Matches.*")
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 2, 3 })
		void methodNameMatches(int unusedParameter) {
		}

	}

	@Nested
	class MisconfigurationTests {

		@Test
		void noContainsNoMatches_configurationException() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(ConfigurationTestCases.class, "noContainsNoMatches", "java.lang.String");

			assertThat(results.numberOfFailedTests()).isEqualTo(1);
		}

		@Test
		void containsAndMatches_contains_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(ConfigurationTestCases.class, "containsAndMatches_contains", "int");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(3);
			assertThat(results.numberOfSkippedTests()).isEqualTo(2);
		}

		@Test
		void containsAndMatches_matches_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(ConfigurationTestCases.class, "containsAndMatches_matches", "int");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(2);
			assertThat(results.numberOfSkippedTests()).isEqualTo(3);
		}

		@Test
		void containsAndMatches_containsAndMatches_correctTestsSkipped() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(ConfigurationTestCases.class, "containsAndMatches_containsAndMatches", "int");

			assertThat(results.numberOfFailedTests()).isEqualTo(0);
			assertThat(results.numberOfSucceededTests()).isEqualTo(1);
			assertThat(results.numberOfSkippedTests()).isEqualTo(4);
		}

	}

	static class ConfigurationTestCases {

		@DisableIfDisplayName
		@ParameterizedTest
		@ValueSource(strings = "a string")
		void noContainsNoMatches(String reason) {
			fail("Test should never have been executed because of misconfiguration.");
		}

		@DisableIfDisplayName(contains = { "1", "2" }, matches = "\\w*")
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 2, 3, 4, 5 })
		void containsAndMatches_contains(int number) {
			if (number == 1 || number == 2)
				fail("Test should've been disabled for " + number);
		}

		@DisableIfDisplayName(contains = "  ", matches = { ".*10[^0]*", ".*10{3,4}[^0]*" })
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 10, 100, 1_000, 10_000 })
		void containsAndMatches_matches(int number) {
			if (number == 10 || number == 1_000 || number == 10_000)
				fail("Test should've been disabled for " + number);
		}

		@DisableIfDisplayName(contains = "000", matches = ".*10?" )
		@ParameterizedTest(name = "See if enabled with {0}")
		@ValueSource(ints = { 1, 10, 100, 1_000, 10_000 })
		void containsAndMatches_containsAndMatches(int number) {
			if (number == 10 || number == 1_000 || number == 10_000)
				fail("Test should've been disabled for " + number);
		}
	}

}

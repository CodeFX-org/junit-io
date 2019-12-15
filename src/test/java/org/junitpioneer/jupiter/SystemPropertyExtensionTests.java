/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.test.event.ExecutionEvent;
import org.junit.platform.engine.test.event.ExecutionEventRecorder;
import org.junitpioneer.AbstractPioneerTestEngineTests;

@DisplayName("SystemProperty extension")
class SystemPropertyExtensionTests extends AbstractPioneerTestEngineTests {

	@BeforeAll
	static void globalSetUp() {
		System.setProperty("set prop A", "old A");
		System.setProperty("set prop B", "old B");
		System.setProperty("set prop C", "old C");

		System.clearProperty("clear prop D");
		System.clearProperty("clear prop E");
		System.clearProperty("clear prop F");
	}

	@AfterAll
	static void globalTearDown() {
		assertThat(System.getProperty("set prop A")).isEqualTo("old A");
		assertThat(System.getProperty("set prop B")).isEqualTo("old B");
		assertThat(System.getProperty("set prop C")).isEqualTo("old C");

		assertThat(System.getProperty("clear prop D")).isNull();
		assertThat(System.getProperty("clear prop E")).isNull();
		assertThat(System.getProperty("clear prop F")).isNull();
	}

	@Nested
	@DisplayName("used with ClearSystemProperty")
	@ClearSystemProperty(key = "set prop A")
	class ClearSystemPropertyTests {

		@Test
		@DisplayName("should clear system property")
		@ClearSystemProperty(key = "set prop B")
		void shouldClearSystemProperty() {
			assertThat(System.getProperty("set prop A")).isNull();
			assertThat(System.getProperty("set prop B")).isNull();
			assertThat(System.getProperty("set prop C")).isEqualTo("old C");

			assertThat(System.getProperty("clear prop D")).isNull();
			assertThat(System.getProperty("clear prop E")).isNull();
			assertThat(System.getProperty("clear prop F")).isNull();
		}

		@Test
		@DisplayName("should be repeatable")
		@ClearSystemProperty(key = "set prop B")
		@ClearSystemProperty(key = "set prop C")
		void shouldBeRepeatable() {
			assertThat(System.getProperty("set prop A")).isNull();
			assertThat(System.getProperty("set prop B")).isNull();
			assertThat(System.getProperty("set prop C")).isNull();

			assertThat(System.getProperty("clear prop D")).isNull();
			assertThat(System.getProperty("clear prop E")).isNull();
			assertThat(System.getProperty("clear prop F")).isNull();
		}

	}

	@Nested
	@DisplayName("used with SetSystemProperty")
	@SetSystemProperty(key = "set prop A", value = "new A")
	class SetSystemPropertyTests {

		@Test
		@DisplayName("should set system property to value")
		@SetSystemProperty(key = "set prop B", value = "new B")
		void shouldSetSystemPropertyToValue() {
			assertThat(System.getProperty("set prop A")).isEqualTo("new A");
			assertThat(System.getProperty("set prop B")).isEqualTo("new B");
			assertThat(System.getProperty("set prop C")).isEqualTo("old C");

			assertThat(System.getProperty("clear prop D")).isNull();
			assertThat(System.getProperty("clear prop E")).isNull();
			assertThat(System.getProperty("clear prop F")).isNull();
		}

		@Test
		@DisplayName("should be repeatable")
		@SetSystemProperty(key = "set prop B", value = "new B")
		@SetSystemProperty(key = "clear prop D", value = "new D")
		void shouldBeRepeatable() {
			assertThat(System.getProperty("set prop A")).isEqualTo("new A");
			assertThat(System.getProperty("set prop B")).isEqualTo("new B");
			assertThat(System.getProperty("set prop C")).isEqualTo("old C");

			assertThat(System.getProperty("clear prop D")).isEqualTo("new D");
			assertThat(System.getProperty("clear prop E")).isNull();
			assertThat(System.getProperty("clear prop F")).isNull();
		}

	}

	@Nested
	@DisplayName("used with both ClearSystemProperty and SetSystemProperty")
	@ClearSystemProperty(key = "set prop A")
	@SetSystemProperty(key = "clear prop D", value = "new D")
	class CombinedSystemPropertyTests {

		@Test
		@DisplayName("should be combinable")
		@ClearSystemProperty(key = "set prop B")
		@SetSystemProperty(key = "clear prop E", value = "new E")
		void clearAndSetSystemPropertyShouldBeCombinable() {
			assertThat(System.getProperty("set prop A")).isNull();
			assertThat(System.getProperty("set prop B")).isNull();
			assertThat(System.getProperty("set prop C")).isEqualTo("old C");

			assertThat(System.getProperty("clear prop D")).isEqualTo("new D");
			assertThat(System.getProperty("clear prop E")).isEqualTo("new E");
			assertThat(System.getProperty("clear prop F")).isNull();
		}

		@Test
		@DisplayName("method level should overwrite class level")
		@ClearSystemProperty(key = "clear prop D")
		@SetSystemProperty(key = "set prop A", value = "new A")
		void methodLevelShouldOverwriteClassLevel() {
			assertThat(System.getProperty("set prop A")).isEqualTo("new A");
			assertThat(System.getProperty("set prop B")).isEqualTo("old B");
			assertThat(System.getProperty("set prop C")).isEqualTo("old C");

			assertThat(System.getProperty("clear prop D")).isNull();
			assertThat(System.getProperty("clear prop E")).isNull();
			assertThat(System.getProperty("clear prop F")).isNull();
		}

	}

	@Nested
	@DisplayName("used with incorrect configuration")
	class ConfigurationFailureTests {

		@Test
		@DisplayName("should fail when clear and set same system property")
		void shouldFailWhenClearAndSetSameSystemProperty() {
			ExecutionEventRecorder eventRecorder = executeTests(MethodLevelInitializationFailureTestCase.class,
				"shouldFailWhenClearAndSetSameSystemProperty");

			assertExtensionConfigurationFailure(eventRecorder.getFailedTestFinishedEvents());
		}

	}

	static class MethodLevelInitializationFailureTestCase {

		@Test
		@ClearSystemProperty(key = "set prop A")
		@SetSystemProperty(key = "set prop A", value = "new A")
		void shouldFailWhenClearAndSetSameSystemProperty() {
		}

	}

	private static void assertExtensionConfigurationFailure(List<ExecutionEvent> failedTestFinishedEvents) {
		assertEquals(1, failedTestFinishedEvents.size());
		//@formatter:off
		Throwable thrown = failedTestFinishedEvents.get(0)
				.getPayload(TestExecutionResult.class)
				.flatMap(TestExecutionResult::getThrowable)
				.orElseThrow(AssertionError::new);
		//@formatter:on
		assertThat(thrown).isInstanceOf(ExtensionConfigurationException.class);
	}

}

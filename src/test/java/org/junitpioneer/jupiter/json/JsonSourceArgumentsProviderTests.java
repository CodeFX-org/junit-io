/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junitpioneer.testkit.assertion.PioneerAssert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.testkit.engine.Event;
import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

/**
 * Tests for {@link JsonSourceArgumentsProvider}
 */
class JsonSourceArgumentsProviderTests {

	@Test
	void assertAllValuesSupplied() {
		ExecutionResults results = PioneerTestKit.executeTestClass(JsonSourceTestCases.class);

		Map<String, List<String>> displayNames = results
				.dynamicallyRegisteredEvents()
				.map(Event::getTestDescriptor)
				.collect(Collectors
						.groupingBy(JsonSourceArgumentsProviderTests::testSourceMethodName,
							Collectors.mapping(TestDescriptor::getDisplayName, Collectors.toList())));

		assertThat(displayNames)
				.containsOnlyKeys("deconstructCustomerFromArray", "deconstructCustomerMultipleFiles", "singleCustomer",
					"customDataLocation");

		assertThat(displayNames.get("deconstructCustomerFromArray")).containsExactly("[1] Luke, 172", "[2] Yoda, 66");

		assertThat(displayNames.get("deconstructCustomerMultipleFiles"))
				.containsExactly("[1] 66, Yoda", "[2] 172, Luke");

		assertThat(displayNames.get("singleCustomer"))
				.containsExactly("[1] Customer{name='Luke', height=172}", "[2] Customer{name='Yoda', height=66}");

		assertThat(displayNames.get("customDataLocation"))
				.containsExactly("[1] Snowspeeder, 4.5", "[2] Imperial Speeder Bike, 3");

	}

	private static String testSourceMethodName(TestDescriptor testDescriptor) {
		return testDescriptor
				.getSource()
				.filter(t -> t instanceof MethodSource)
				.map(t -> (MethodSource) t)
				.orElseThrow(() -> new RuntimeException("No method source"))
				.getMethodName();
	}

	@Nested
	class JsonSourceTestCases {

		@ParameterizedTest
		@JsonSource(resources = "org/junitpioneer/jupiter/json/customers.json")
		void deconstructCustomerFromArray(@Param("name") String name, @Param("height") int height) {
		}

		@ParameterizedTest
		@JsonSource(resources = { "org/junitpioneer/jupiter/json/customer-yoda.json",
				"org/junitpioneer/jupiter/json/customer-luke.json", })
		void deconstructCustomerMultipleFiles(@Param("height") int height, @Param("name") String name) {
		}

		@ParameterizedTest
		@JsonSource(resources = "org/junitpioneer/jupiter/json/customers.json")
		void singleCustomer(Customer customer) {
		}

		@ParameterizedTest
		@JsonSource(resources = { "org/junitpioneer/jupiter/json/customer-luke.json" }, data = "vehicles")
		void customDataLocation(@Param("name") String name, @Param("length") double length) {
		}

	}

	@Nested
	class InvalidJsonSourceTestCases {

		@Test
		void noFilesOrResources() {
			ExecutionResults results = PioneerTestKit.executeTestMethod(InvalidJsonSource.class, "noFilesOrResources");

			assertThat(results)
					.hasSingleFailedContainer()
					.withExceptionInstanceOf(PreconditionViolationException.class)
					.hasMessage("Resources or files must not be empty");
		}

		@Test
		void emptyClasspathResource() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethod(InvalidJsonSource.class, "emptyClasspathResource");

			assertThat(results)
					.hasSingleFailedContainer()
					.withExceptionInstanceOf(PreconditionViolationException.class)
					.hasMessage("Classpath resource must not be null or blank");
		}

		@Test
		void missingClasspathResource() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethod(InvalidJsonSource.class, "missingClasspathResource");

			assertThat(results)
					.hasSingleFailedContainer()
					.withExceptionInstanceOf(PreconditionViolationException.class)
					.hasMessage("Classpath resource [dummy-customer.json] does not exist");
		}

		@Test
		void dataLocationMissing() {
			ExecutionResults results = PioneerTestKit.executeTestMethod(InvalidJsonSource.class, "dataLocationMissing");

			assertThat(results)
					.hasSingleFailedContainer()
					.withExceptionInstanceOf(PreconditionViolationException.class)
					.hasMessageContainingAll("Node ", "does not have data element at dummy");
		}

	}

	static class InvalidJsonSource {

		@JsonSource
		@ParameterizedTest
		void noFilesOrResources() {

		}

		@JsonSource(resources = { "org/junitpioneer/jupiter/json/customer-yoda.json", "" })
		@ParameterizedTest
		void emptyClasspathResource() {

		}

		@JsonSource(resources = "dummy-customer.json")
		@ParameterizedTest
		void missingClasspathResource() {

		}

		@JsonSource(resources = { "org/junitpioneer/jupiter/json/customer-yoda.json", }, data = "dummy")
		@ParameterizedTest
		void dataLocationMissing() {

		}

	}

	static class Customer {

		private String name;
		private int height;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		@Override
		public String toString() {
			return "Customer{" + "name='" + name + '\'' + ", height=" + height + '}';
		}

	}

}

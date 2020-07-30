/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.testkit.assertion.single;

/**
 * Assertions for asserting the state of single tests/containers.
 */
public interface TestCaseAssert {

	/**
	 * Asserts that there was exactly one started test.
	 * @return a {@link TestCaseStartedAssert} for further assertions
	 */
	TestCaseStartedAssert hasSingleStartedTest();

	/**
	 * Asserts that there was exactly one failed test.
	 * @return a {@link TestCaseFailureAssert} for further assertions
	 */
	TestCaseFailureAssert hasSingleFailedTest();

	/**
	 * Asserts that there was exactly one aborted test.
	 */
	void hasSingleAbortedTest();

	/**
	 * Asserts that there was exactly one successful test.
	 */
	void hasSingleSucceededTest();

	/**
	 * Asserts that there was exactly one skipped test.
	 */
	void hasSingleSkippedTest();

	TestCaseStartedAssert hasSingleDynamicallyRegisteredTest();

	TestCaseStartedAssert hasSingleStartedContainer();

	TestCaseFailureAssert hasSingleFailedContainer();

	void hasSingleAbortedContainer();

	void hasSingleSucceededContainer();

	void hasSingleSkippedContainer();

	TestCaseStartedAssert hasSingleDynamicallyRegisteredContainer();

}
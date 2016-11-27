/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.codefx.junit.io.extension;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ContainerExecutionCondition;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionCondition;
import org.junit.jupiter.api.extension.TestExtensionContext;

/**
 * Implements the execution conditions for {@link DisabledOnOs} and {@link EnabledOnOs}.
 */
class OsCondition implements ContainerExecutionCondition, TestExecutionCondition {

	static final String NO_CONDITION_PRESENT = "No OS-specific condition present.";
	static final String TEST_ENABLED = "Test is enabled on %s.";
	static final String TEST_DISABLED = "Test is disabled on %s.";

	/**
	 * Detector for the current OS.
	 */
	private final Supplier<OS> osDetector;

	OsCondition(Supplier<OS> osDetector) {
		this.osDetector = requireNonNull(osDetector);
	}

	OsCondition() {
		this(OS::determine);
	}

	@Override
	public ConditionEvaluationResult evaluate(ContainerExtensionContext context) {
		return evaluateIfAnnotated(context.getElement());
	}

	@Override
	public ConditionEvaluationResult evaluate(TestExtensionContext context) {
		return evaluateIfAnnotated(context.getElement());
	}

	private ConditionEvaluationResult evaluateIfAnnotated(Optional<AnnotatedElement> element) {
		// @formatter:off
		Optional<ConditionEvaluationResult> disabledResult = element
				.flatMap(el -> findAnnotation(el, DisabledOnOs.class))
				.map(DisabledOnOs::value)
				.map(this::disabledIfOn);
		// @formatter:on
		if (disabledResult.isPresent())
			return disabledResult.get();

		// @formatter:off
		Optional<ConditionEvaluationResult> enabledResult = element
				.flatMap(el -> findAnnotation(el, EnabledOnOs.class))
				.map(EnabledOnOs::value)
				// invert the selection to disable the test
				// if it NOT one of those given to the annotation
				.map(OS::except)
				.map(this::disabledIfOn);
		// @formatter:on
		if (enabledResult.isPresent())
			return enabledResult.get();

		// if both annotations are missing, the container/test is enabled
		return ConditionEvaluationResult.enabled(NO_CONDITION_PRESENT);
	}

	private ConditionEvaluationResult disabledIfOn(OS[] disabledOnOs) {
		OS os = osDetector.get();
		if (Arrays.asList(disabledOnOs).contains(os))
			return ConditionEvaluationResult.disabled(format(TEST_DISABLED, os));
		else
			return ConditionEvaluationResult.enabled(format(TEST_ENABLED, os));
	}

}
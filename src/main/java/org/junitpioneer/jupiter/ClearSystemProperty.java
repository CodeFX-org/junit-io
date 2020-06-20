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

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * {@code @ClearSystemProperty} is a JUnit Jupiter extension to clear the value
 * of a system property for a test execution.
 *
 * <p>The key of the system property to be cleared must be specified via
 * {@link #key()}. After the annotated element has been executed, After the
 * annotated method has been executed, the initial default value is restored.</p>
 *
 * <p>{@code ClearSystemProperty} is repeatable and can be used on the method and
 * on the class level. If a class is annotated, the configured property will be
 * cleared for all tests inside that class.</p>
 *
 * <p>During
 * <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution" target="_top">parallel test execution</a>,
 * all tests annotated with {@link ClearSystemProperty}, {@link SetSystemProperty}, {@link ReadsSystemProperty}, and {@link WritesSystemProperty}
 * are scheduled in a way that guarantees correctness under mutation of shared global state.
 * </p>
 *
 * <p>For more details and examples, see
 * <a href="https://junit-pioneer.org/docs/system-properties/" target="_top">the documentation on <code>@ClearSystemProperty and @SetSystemProperty</code></a>.
 * </p>
 *
 * @since 0.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Repeatable(ClearSystemProperty.ClearSystemProperties.class)
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
@ExtendWith(SystemPropertyExtension.class)
public @interface ClearSystemProperty {

	/**
	 * The key of the system property to be cleared.
	 */
	String key();

	/**
	 * Containing annotation of repeatable {@code @ClearSystemProperty}.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
	@ExtendWith(SystemPropertyExtension.class)
	@interface ClearSystemProperties {

		ClearSystemProperty[] value();

	}

}

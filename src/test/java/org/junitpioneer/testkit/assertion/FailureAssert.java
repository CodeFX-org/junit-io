/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.testkit.assertion;

import org.assertj.core.api.AbstractThrowableAssert;

/**
 * Used to assert failed containers/tests.
 */
public interface FailureAssert {

	AbstractThrowableAssert<?, ? extends Throwable> withException(Class<? extends Throwable> exceptionType);

	AbstractThrowableAssert<?, ? extends Throwable> withException();

}
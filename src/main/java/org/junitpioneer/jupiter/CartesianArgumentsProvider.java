/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter;

import java.lang.reflect.Parameter;
import java.util.function.Consumer;

import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * If you are implementing an {@link org.junit.jupiter.params.provider.ArgumentsProvider ArgumentsProvider}
 * for {@link CartesianProductTest}, it has to implement this interface <b>instead</b> to know which parameter it provides
 * arguments to. For more information, see
 * <a href="https://junit-pioneer.org/docs/cartesian-product/" target="_top">the Cartesian product documentation</a>.
 *
 * @see org.junit.jupiter.params.provider.ArgumentsProvider
 * @see CartesianProductTestExtension
 */
public interface CartesianArgumentsProvider extends Consumer<Parameter>, ArgumentsProvider {
}

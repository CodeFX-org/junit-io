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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.PreconditionViolationException;

/**
 * This is basically a copy of Jupiter's {@code EnumArgumentsProvider},
 * except it does NOT support {@code @ParameterizedTest}.
 */
class CartesianEnumArgumentsProvider implements CartesianAnnotationConsumer<CartesianEnumSource>, ArgumentsProvider {

	private CartesianEnumSource enumSource;

	@Override
	public void accept(CartesianEnumSource enumSource) {
		this.enumSource = enumSource;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		Set<? extends Enum<?>> constants = getEnumConstants(context);
		CartesianEnumSource.Mode mode = enumSource.mode();
		String[] declaredConstantNames = enumSource.names();
		if (declaredConstantNames.length > 0) {
			Set<String> uniqueNames = stream(declaredConstantNames).collect(toSet());

			if (uniqueNames.size() != declaredConstantNames.length)
				throw new PreconditionViolationException("Duplicate enum constant name(s) found in " + enumSource);

			mode.validate(enumSource, constants, uniqueNames);
			constants.removeIf(constant -> !mode.select(constant, uniqueNames));
		}
		return constants.stream().map(Arguments::of);
	}

	private <E extends Enum<E>> Set<? extends E> getEnumConstants(ExtensionContext context) {
		Class<E> enumClass = determineEnumClass(context);
		return EnumSet.allOf(enumClass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <E extends Enum<E>> Class<E> determineEnumClass(ExtensionContext context) {
		Class enumClass = enumSource.value();
		if (enumClass.equals(NullEnum.class)) {
			Method method = context.getRequiredTestMethod();
			Class<?>[] parameterTypes = method.getParameterTypes();

			if (parameterTypes.length <= 0)
				throw new PreconditionViolationException(
					"Test method must declare at least one parameter: " + method.toGenericString());

			if (!Enum.class.isAssignableFrom(parameterTypes[0]))
				throw new PreconditionViolationException(
					"First parameter must reference an Enum type (alternatively, use the annotation's 'value' attribute to specify the type explicitly): "
							+ method.toGenericString());

			enumClass = parameterTypes[0];
		}
		return enumClass;
	}

}

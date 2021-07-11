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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Add a new method - Object[] arguments() - to allow e.g. a @New(TemporaryFile) to
//       specify its file name suffix. https://github.com/junit-pioneer/junit-pioneer/issues/348#issuecomment-850816098

@Retention(RetentionPolicy.RUNTIME)
// TODO: Consider adding and testing ElementType.FIELD
@Target(ElementType.PARAMETER)
public @interface New {

	Class<? extends ResourceFactory<?>> value();

}

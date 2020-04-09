/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.utils;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.platform.engine.test.event.ExecutionEvent;
import org.junit.platform.engine.test.event.ExecutionEventRecorder;

/**
 * Util class for the ExecutionEventRecorder class.
 */
public class EventRecorderUtils {

	/**
	 * Collects all published report entries from an execution event recorder.
	 *
	 * @param recorder Recorder which contains the report entries
	 * @return List with all published report entries
	 */
	public static List<Map<String, String>> reportEntries(ExecutionEventRecorder recorder) {
		return recorder
				.eventStream()
				.filter(event -> event.getType().equals(ExecutionEvent.Type.REPORTING_ENTRY_PUBLISHED))
				.map(executionEvent -> executionEvent.getPayload(org.junit.platform.engine.reporting.ReportEntry.class))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(org.junit.platform.engine.reporting.ReportEntry::getKeyValuePairs)
				.collect(toList());
	}

	/**
	 * Returns the first report entry from the execution event recorder.
	 *
	 * @param recorder Recorder which contains the report entries
	 * @return first recorded entry or empty list if no entry was recorded
	 */
	public static Map<String, String> getFirstReportEntry(ExecutionEventRecorder recorder) {
		List<Map<String, String>> reportEntries = reportEntries(recorder);

		if (null != reportEntries && reportEntries.size() > 0) {
			return reportEntries.get(0);
		}

		return Collections.<String, String> emptyMap();
	}

}
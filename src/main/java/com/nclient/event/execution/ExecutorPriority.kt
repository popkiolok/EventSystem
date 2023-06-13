package com.nclient.event.execution;

import com.nclient.event.Event;

/**
 * Allows controlling executors call order. {@link EventExecutor}s with higher
 * {@link ExecutorPriority} called firstly. If the {@link Event} is cancelled executors with not
 * enough high priorities might not be called.
 *
 * @author NassyLove
 * @since 0.0.1
 */
public enum ExecutorPriority {
	HIGHEST(0L),
	HIGH(Long.MAX_VALUE / 5),
	DEFAULT(2L * (Long.MAX_VALUE / 5)),
	LOW(3L * (Long.MAX_VALUE / 5)),
	LOWEST((4L * (Long.MAX_VALUE / 5)));

	private final long value;

	ExecutorPriority(final long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}
}

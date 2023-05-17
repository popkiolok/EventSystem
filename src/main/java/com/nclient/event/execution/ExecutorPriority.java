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
	// Fields order in enum is important because used for sorting listeners.
	HIGHEST,
	HIGH,
	DEFAULT,
	LOW,
	LOWEST
}

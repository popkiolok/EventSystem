package com.nclient.event.execution;

/**
 * Exception that throws event executor if an error occurred while executing.
 *
 * @author NassyLove
 * @since 1.0.0
 */
public class EventExecutorException extends Exception {
	EventExecutorException(final EventExecutor executor, final Throwable cause) {
		super(String.format("An error occurred while executing %s.",
				executor.getName()), cause);
	}
}

package com.nclient.event;

import java.util.function.BooleanSupplier;

/**
 * Contains information about event and provides cancel callback if event is cancellable.
 *
 * @author NassyLove
 * @since 0.0.1
 */
public class Event {
	private boolean cancel;

	// TODO might create EventCancellable?
	public void cancel() {
		assert getClass().isAnnotationPresent(Cancellable.class) : "This event is not cancellable!";
		cancel = true;
	}

	public void cancelIf(final BooleanSupplier condition) {
		if (condition.getAsBoolean()) {
			cancel();
		}
	}

	public boolean isCancelled() {
		return cancel;
	}
}

package com.nclient.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventTest {
	private CancellableEvent cancellableEventUnderTest;
	private NotCancellableEvent notCancellableEventUnderTest;

	private static class CancellableEvent extends Cancellable {
	}

	private static class NotCancellableEvent extends Event {
	}

	@BeforeEach
	void setUp() {
		cancellableEventUnderTest = new CancellableEvent();
		notCancellableEventUnderTest = new NotCancellableEvent();
	}

	@Test
	void testCancelCancellable() {
		// Setup
		// Run the test
		cancellableEventUnderTest.cancel();

		// Verify the results
		assertTrue(cancellableEventUnderTest.getCancelled());
	}

	@Test
	void testCancelNotCancellable() {
		// Verify the results
		assertFalse(notCancellableEventUnderTest.getCancelled());
	}

	@Test
	void testCancelIfCancellable() {
		// Setup
		// Run the test
		cancellableEventUnderTest.cancelIf(() -> true);

		// Verify the results
		assertTrue(cancellableEventUnderTest.getCancelled());
	}

	@Test
	void testGetCancelled() {
		assertFalse(cancellableEventUnderTest.getCancelled());
	}
}

package com.nclient.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventTest {
	private Event cancellableEventUnderTest;
	private Event notCancellableEventUnderTest;

	@Cancellable
	private static class CancellableEvent extends Event {
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
		assertTrue(cancellableEventUnderTest.isCancelled());
	}

	@Test
	void testCancelNotCancellable() {
		// Verify the results
		assertThrows(Throwable.class, notCancellableEventUnderTest::cancel);
		assertFalse(cancellableEventUnderTest.isCancelled());
	}

	@Test
	void testCancelIfCancellable() {
		// Setup
		// Run the test
		cancellableEventUnderTest.cancelIf(() -> true);

		// Verify the results
		assertTrue(cancellableEventUnderTest.isCancelled());
	}

	@Test
	void testIsCancelled() {
		assertFalse(cancellableEventUnderTest.isCancelled());
	}
}

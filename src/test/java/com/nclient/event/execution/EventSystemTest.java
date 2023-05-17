package com.nclient.event.execution;

import com.nclient.event.Cancellable;
import com.nclient.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventSystemTest {
	@Mock
	private Consumer<EventExecutorException> mockErrorAction;

	@Cancellable
	private static class TestEvent extends Event {
	}

	private EventSystem eventSystemUnderTest;
	private Listener testListener;
	private boolean listenerCalled;
	private boolean taskCalled;

	@BeforeEach
	void setUp() {
		initMocks(this);
		eventSystemUnderTest = new EventSystem(mockErrorAction);
		testListener = new Listener(event -> listenerCalled = true, TestEvent.class,
				ExecutorPriority.DEFAULT);
		eventSystemUnderTest.attach(testListener);
		eventSystemUnderTest.attach(
				new Task(event -> taskCalled = true, TestEvent.class, ExecutorPriority.DEFAULT,
						0));
		listenerCalled = taskCalled = false;
	}

	@Test
	void testCall() {
		// Setup
		final Event event = new TestEvent();

		// Run the test
		final boolean result = eventSystemUnderTest.call(event);

		// Verify the results
		assertFalse(result);
	}


	@Test
	@DisplayName(
			"given manager with high priority listener that cancel event when call event " +
					"event is cancelled and other executors is not called")
	void testCallCancel() {
		// Setup
		final Event event = new TestEvent();
		eventSystemUnderTest.attach(
				new Listener(Event::cancel, TestEvent.class, ExecutorPriority.HIGHEST));
		// Run the test
		final boolean result = eventSystemUnderTest.call(event);

		// Verify the results
		assertTrue(result);
		assertFalse(listenerCalled);
		assertFalse(taskCalled);
	}

	@Test
	void testCountListeners() {
		// Run the test
		final int result = eventSystemUnderTest.countListeners();

		// Verify the results
		assertEquals(1, result);
	}

	@Test
	void testCountTasks() {
		// Run the test
		final int result = eventSystemUnderTest.countTasks();

		// Verify the results
		assertEquals(1, result);
	}

	@Test
	void testAttach() {
		// Run the test
		eventSystemUnderTest.call(new TestEvent());

		// Verify the results
		assertTrue(listenerCalled);
	}

	@Test
	void testDetach() {
		// Run the test
		eventSystemUnderTest.detach(testListener);
		eventSystemUnderTest.call(new TestEvent());

		// Verify the results
		assertFalse(listenerCalled);
	}
}
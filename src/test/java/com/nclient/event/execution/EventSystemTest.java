package com.nclient.event.execution;

import com.nclient.event.Cancellable;
import com.nclient.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventSystemTest {
	private static class TestEvent extends Cancellable {
	}

	private final EventSystem eventSystemUnderTest = new EventSystem(Throwable::printStackTrace);
	private Listener<TestEvent> testListener;
	private boolean listenerCalled;
	private boolean taskCalled;

	@BeforeEach
	void setUp() {
		initMocks(this);
		EventContainer containerUnderTest = new EventContainer(eventSystemUnderTest);

		testListener = Listener.listener(TestEvent.class, ExecutorPriority.DEFAULT,
				event -> listenerCalled = true);
		containerUnderTest.attach(testListener);

		final Task<?> testTask =
				Task.task(TestEvent.class, ExecutorPriority.DEFAULT, 0, event -> taskCalled = true);
		containerUnderTest.attach(testTask);

		listenerCalled = taskCalled = false;
	}

	@AfterEach
	void cleanUp() {
		EventSystem.setABSTRACT_EVENTS_SUPPORT(false);
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
	@DisplayName("given manager with high priority listener that cancel event when call event " +
			"event is cancelled and other executors is not called")
	void testCallCancel() {
		// Setup
		final Event event = new TestEvent();
		eventSystemUnderTest.attach(
				Listener.listener(TestEvent.class, ExecutorPriority.HIGHEST, TestEvent::cancel));
		// Run the test
		final boolean result = eventSystemUnderTest.call(event);

		// Verify the results
		assertTrue(result);
		assertFalse(listenerCalled);
		assertFalse(taskCalled);
	}

	@Test
	void testCallOrder() {
		final String[] ref = {""};
		Stream.of(Listener.listener(TestEvent.class, ExecutorPriority.HIGHEST, event -> ref[0] += "1"),
						Listener.listener(TestEvent.class, ExecutorPriority.HIGH, event -> ref[0] += "2"),
						Listener.listener(TestEvent.class, ExecutorPriority.DEFAULT, event -> ref[0] += "3"),
						Listener.listener(TestEvent.class, ExecutorPriority.LOW, event -> ref[0] += "4"),
						Listener.listener(TestEvent.class, ExecutorPriority.LOWEST, event -> ref[0] += "5"))
				.forEach(eventSystemUnderTest::attach);

		eventSystemUnderTest.call(new TestEvent());

		assertEquals("12345", ref[0]);
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
	void testAttachListener() {
		// Run the test
		eventSystemUnderTest.call(new TestEvent());

		// Verify the results
		assertTrue(listenerCalled);
	}

	@Test
	void testAttachTask() {
		// Run the test
		eventSystemUnderTest.call(new TestEvent());

		// Verify the results
		assertTrue(taskCalled);
	}

	@Test
	void testDetach() {
		// Run the test
		eventSystemUnderTest.detach(testListener);
		eventSystemUnderTest.call(new TestEvent());

		// Verify the results
		assertFalse(listenerCalled);
	}

	@Test
	void testSetAbstractEventsSupport() {
		EventSystem.setABSTRACT_EVENTS_SUPPORT(true);

		assertDoesNotThrow(() -> Listener.listener(Event.class, ExecutorPriority.DEFAULT, event -> {
		}));
	}
}
package com.nclient.event.execution

import com.nclient.event.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.Method

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventContainerTest {

	private lateinit var containerUnderTest: EventContainer

	private lateinit var eventSystem: EventSystem
	private lateinit var testListener: Listener<TestEvent>

	@BeforeEach
	fun setup() {
		eventSystem = EventSystem {}
		containerUnderTest = EventContainer(eventSystem)
		testListener = Listener(TestEvent::class, ExecutorPriority.DEFAULT) { _ -> }
	}

	@Test
	fun `when create container from parent then container has the same event system as parent`() {
		val container = EventContainer("TestContainerChild", containerUnderTest)

		assertEquals(containerUnderTest.eventSystem, container.eventSystem)
	}

	@Test
	fun `when detach some listener it will be detached from event container and removed from event system`() {
		containerUnderTest.attach(testListener)
		containerUnderTest.detach(testListener)
		assertEquals(0, eventSystem.countListeners())
	}

	@Test
	fun `when detach all detach this container executors`() {
		containerUnderTest.attach(testListener)
		containerUnderTest.detachAll()
		assertEquals(0, eventSystem.countListeners())
	}

	@Test
	fun `when detach all detach child container executors`() {
		val childContainer = EventContainer(eventSystem, containerUnderTest)
		childContainer.attach(testListener)
		containerUnderTest.detachAll()
		assertEquals(0, eventSystem.countListeners())
	}

	@Test
	fun `when attach some listener it will be added to event system and attached to event container`() {        // Run test
		containerUnderTest.attach(testListener)

		// Verify the results
		assertTrue(testListener.isAttachedTo(containerUnderTest))
		assertEquals(1, eventSystem.countListeners())
	}

	@Test
	fun `when attach all event listeners are attached and handle events`() {
		val classWithListener = ClassWithListener()
		val m = classWithListener.javaClass.getDeclaredMethod("eventListenerUnderTest",
			TestEvent::class.java)
		m.isAccessible = true
		val info = m.getAnnotation(EventListener::class.java)

		val map = HashMap<Method, EventListener>()
		map += m to info
		containerUnderTest.attachAll(map, classWithListener)
		eventSystem.call(TestEvent())

		assertTrue(classWithListener.success)
	}

	@Test
	fun `test getEventListeners finds annotated methods`() {
		// Define a test class with annotated methods
		class TestClass {
			@EventListener
			fun onTestEvent(event: TestEvent) {
			}

			@EventListener(priority = ExecutorPriority.HIGH)
			fun onOtherEvent(event: TestEvent, executor: EventExecutor<TestEvent>) {
			}
		}

		// Get the annotated methods from the test class
		val eventListeners = getEventListeners(TestClass::class.java)

		// Test that both methods are found
		assertEquals(2, eventListeners.size)
	}

	@Test
	fun `test getEventListeners handles bad parameter count`() {
		// Define a test class with an annotated method with too many parameters
		class TestClass {
			@EventListener
			fun onTestEvent(event: TestEvent, otherParam: EventExecutor<TestEvent>, other: Any) {
			}
		}

		// Test that an exception is thrown with a descriptive error message
		val exception = assertThrows<AssertionError> {
			getEventListeners(TestClass::class.java)
		}
		assertEquals(
			"Bad EventExecutor onTestEvent in ${TestClass::class.java.name} number of parameters: 3.",
			exception.message)
	}

	@Test
	fun `test getEventListeners handles bad parameter types`() {
		// Define a test class with an annotated method with incorrect parameter types
		class TestClass {
			@EventListener
			fun onTestEvent(otherEvent: String) {
			}
		}
		// Test that an exception is thrown with a descriptive error message
		val exception = assertThrows<AssertionError> {
			getEventListeners(TestClass::class.java)
		}
		assertEquals(
			"Bad EventExecutor onTestEvent in ${TestClass::class.java.name} first parameter type.",
			exception.message)
	}

	@Test
	fun `test getEventListeners handles bad parameter types with executor`() {
		// Define a test class with an annotated method with incorrect parameter types and an executor
		class TestClass {
			@EventListener
			fun onTestEvent(event: TestEvent, otherParam: Any) {
			}
		}

		// Test that an exception is thrown with a descriptive error message
		val exception = assertThrows<AssertionError> {
			getEventListeners(TestClass::class.java)
		}
		assertEquals(
			"Bad EventExecutor onTestEvent in ${TestClass::class.java.name} second parameter type.",
			exception.message)
	}

	private class TestEvent : Event()

	class ClassWithListener {
		var success = false

		@EventListener
		private fun eventListenerUnderTest(event: TestEvent) {
			success = true
		}
	}
}

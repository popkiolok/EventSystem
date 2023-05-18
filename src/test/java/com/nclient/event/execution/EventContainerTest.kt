package com.nclient.event.execution

import com.nclient.event.Event
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

/**
 * @author NassyLove
 * @since 1.0.0
 */
class EventContainerTest {

	private class TestEvent : Event()

	private lateinit var containerUnderTest: EventContainer
	private lateinit var eventSystem: EventSystem
	private lateinit var testListener: Listener

	@BeforeEach
	fun setup() {
		eventSystem = EventSystem {}
		containerUnderTest = EventContainer(eventSystem)
		testListener = Listener({ _ -> }, TestEvent::class.java, ExecutorPriority.DEFAULT)
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
	fun `when attach listener with container method then it is attached to container and event system`() {
		containerUnderTest.listener({}, TestEvent::class.java)

		assertEquals(1, eventSystem.countListeners())
	}

	@Test
	fun `when attach task with container method then it is attached to container and event system`() {
		containerUnderTest.task({}, TestEvent::class.java)

		assertEquals(1, eventSystem.countTasks())
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

	class ClassWithListener {
		var success = false

		@EventListener
		private fun eventListenerUnderTest(event: TestEvent) {
			success = true
		}
	}
}
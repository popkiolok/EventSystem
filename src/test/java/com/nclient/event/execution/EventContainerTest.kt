package com.nclient.event.execution

import com.nclient.event.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
		testListener = Listener({_ -> }, TestEvent::class.java, ExecutorPriority.DEFAULT)
	}

	@Test
	fun `when detach some listener it will be detached from event container and removed from event manager`() {
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
	fun `when attach some listener it will be added to event manager and attached to event container`() {
		// Run test
		containerUnderTest.attach(testListener)

		// Verify the results
		assertTrue(testListener.isAttachedTo(containerUnderTest))
		assertEquals(1, eventSystem.countListeners())
	}
}
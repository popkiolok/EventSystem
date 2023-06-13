package com.nclient.event.execution

import com.nclient.event.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author NassyLove
 * @since 1.0.1
 */
class EventExecutorTest {
	private class TestEvent : Event()
	private class TestCause : Throwable()

	private lateinit var system: EventSystem
	private lateinit var container: EventContainer

	@BeforeEach
	fun setup() {
		system = EventSystem {}
		container = EventContainer("TestContainer", system)
	}

	@Test
	fun `when accept event and throw exception in handling then executor throw EventExecutorException`() {
		val executor = Listener(TestEvent::class) { _ -> throw TestCause() }
		container.attach(executor)

		assertThrows(EventExecutorException::class.java) {
			executor.accept(TestEvent())
		}
	}

	@Test
	fun `when detach then event is not attached to event system`() {
		val executor = Listener(TestEvent::class) { _ -> }
		container.attach(executor)

		executor.detach()

		assertEquals(0, system.countListeners())
	}
}
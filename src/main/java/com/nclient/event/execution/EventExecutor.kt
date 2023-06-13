package com.nclient.event.execution

import com.nclient.event.Event
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * [EventExecutor] that can be [Listener] or [Task].
 *
 * @author NassyLove
 * @since 1.0.0
 */
abstract class EventExecutor<T : Event> {
	private val action: (T) -> Unit
	val type: KClass<T>

	/** The [EventContainer] that this listener is attached to. */
	protected var container: EventContainer? = null

	/**
	 * Depends on [ExecutorPriority]. Executors with higher priority will be
	 * called firstly.
	 */
	val priority: ULong

	/**
	 * Creates new event executor.
	 *
	 * @param type The class of the listening event. If abstract events are not
	 *     supported, it should not be abstract.
	 * @param priority The event executor priority.
	 * @param action The action will be performed on event call.
	 */
	constructor(type: KClass<T>, priority: ExecutorPriority, action: (T) -> Unit) {
		assert(EventSystem.ABSTRACT_EVENTS_SUPPORT || !Modifier.isAbstract(type.java.modifiers)) {
			"Unable to create event listener with abstract event type."
		}
		this.action = action
		this.type = type
		this.priority = priority.value + EventSystem.nextExecutorId++
	}

	constructor(type: KClass<T>, priority: ExecutorPriority,
				action: (T, EventExecutor<T>) -> Unit) {
		assert(EventSystem.ABSTRACT_EVENTS_SUPPORT || !Modifier.isAbstract(type.java.modifiers)) {
			"Unable to create event listener with abstract event type."
		}
		this.action = { event -> action.invoke(event, this) }
		this.type = type
		this.priority = priority.value + EventSystem.nextExecutorId++
	}

	open fun accept(event: Event) {
		try {
			action.invoke(type.cast(event))
		} catch (e: Throwable) {
			throw EventExecutorException(name, e)
		}
	}

	abstract val name: String

	/**
	 * Attach event executor to specified [EventContainer].
	 *
	 * @param container The [EventContainer] to attach to.
	 */
	fun attachTo(container: EventContainer) {
		assert(this.container == null) {
			"Reattaching or double attaching the same executor is not supported."
		}
		this.container = container
	}

	/**
	 * Gets is this event executor is attached to specified [EventContainer].
	 *
	 * @param container The [EventContainer] to check for.
	 * @return True if this event executor is attached to specified
	 *     [EventContainer] false otherwise.
	 */
	fun isAttachedTo(container: EventContainer): Boolean {
		return this.container == container
	}

	/**
	 * Detach [EventExecutor] from the [EventContainer].
	 *
	 * @throws NullPointerException If the [EventExecutor] is not attached to
	 *     any [EventContainer].
	 */
	fun detach() {
		container!!.detach(this)
	}
}

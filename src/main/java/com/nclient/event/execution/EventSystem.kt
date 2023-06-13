package com.nclient.event.execution

import com.google.common.collect.MultimapBuilder
import com.google.common.collect.SetMultimap
import com.nclient.event.Event
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * Allows handling and calling events.
 *
 * @author NassyLove
 * @since 1.0.0
 */
class EventSystem(private val errorAction: Consumer<EventExecutorException>) {
	val executors: SetMultimap<KClass<out Event>, EventExecutor<*>> =
		MultimapBuilder.hashKeys().treeSetValues { a: EventExecutor<*>, b: EventExecutor<*> ->
				a.priority.compareTo(b.priority)
			}.build()
	private val toRemove: Queue<EventExecutor<*>> = LinkedList()
	private var listeners = 0
	private var tasks = 0

	/**
	 * Call this [Event] event listeners.
	 *
	 * @param event The [Event] to call.
	 * @return true if event is canceled, false otherwise.
	 */
	fun call(event: Event): Boolean {
		synchronized(toRemove) {
			while (!toRemove.isEmpty()) {
				val executor = toRemove.poll()
				executors.remove(executor.type, executor)
			}
		}
		for (executor in executors[event::class]) {
			try {
				executor.accept(event)
				if (event.cancelled) {
					return true
				}
			} catch (e: EventExecutorException) {
				errorAction.accept(e)
			}
		}
		return false
	}

	/**
	 * Gets the number of listeners currently attached.
	 *
	 * @return The number of listeners currently attached.
	 */
	fun countListeners(): Int {
		return listeners
	}

	/**
	 * Gets the number of tasks currently attached.
	 *
	 * @return The number of tasks currently attached.
	 */
	fun countTasks(): Int {
		return tasks
	}

	/**
	 * Attach [EventExecutor] to this [EventSystem].
	 *
	 * @param executor The [EventExecutor] to attach.
	 */
	fun attach(executor: EventExecutor<*>) {
		executors.put(executor.type, executor) // TODO is such adding thread safe?
		if (executor is Listener<*>) {
			listeners++
		} else if (executor is Task) {
			tasks++
		}
	}

	/**
	 * Enqueue [EventExecutor] to be detached from this [EventSystem]. After
	 * this method is called, there is guaranty that executor will not be
	 * called until it will be reattached to this or another [EventSystem].
	 *
	 * @param executor The [EventExecutor] to remove.
	 */
	fun detach(executor: EventExecutor<*>) {
		toRemove.offer(executor)
		if (executor is Listener<*>) {
			listeners--
		} else if (executor is Task<*>) {
			tasks--
		}
	}

	companion object {
		/**
		 * Allows [EventSystem]s to support abstract [Event]s. When [EventExecutor]
		 * listening abstract event, it will be called when any of abstract event
		 * inheritors are called. By default, abstract events are not supported.
		 */
		@JvmStatic
		var ABSTRACT_EVENTS_SUPPORT = false
		internal var nextExecutorId: ULong = 0u
	}
}
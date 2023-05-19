package com.nclient.event.execution

import com.google.common.annotations.VisibleForTesting
import com.nclient.event.Event
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Contains a group of [EventExecutor]s with the same [EventSystem].
 * [EventExecutor]s with the same [EventContainer] can be dynamically
 * removed from the [EventSystem].
 *
 * @author NassyLove
 * @since 1.0.0
 */
@Suppress("UNCHECKED_CAST")
class EventContainer @JvmOverloads constructor(val name: String,
											   @VisibleForTesting internal val eventSystem: EventSystem,
											   parent: EventContainer? = null) {
	private val children: MutableCollection<EventContainer> = ArrayList()

	constructor(name: String, parent: EventContainer) : this(name, parent.eventSystem, parent)

	init {
		parent?.children?.add(this)
	}

	@JvmOverloads
	constructor(eventSystem: EventSystem, parent: EventContainer? = null) : this(
		"EventContainer #${count++}${if (parent == null) "" else " : " + parent.name}", eventSystem,
		parent)

	private fun isAttached(executor: EventExecutor): Boolean {
		return executor.isAttachedTo(this) || children.stream()
			.anyMatch { child: EventContainer -> child.isAttached(executor) }
	}

	/**
	 * Attach [EventExecutor] to this event container.
	 *
	 * @param executor The [EventExecutor] to attach.
	 */
	fun attach(executor: EventExecutor) {
		eventSystem.attach(executor)
		executor.attachTo(this)
	}

	/**
	 * Attach all declared methods in [obj] class annotated with
	 * [EventListener] to this event container.
	 *
	 * @param methods Method to EventListener annotation map.
	 * @param obj Object to call event for.
	 * @throws IllegalAccessException If method in the map is not accessible.
	 */
	fun attachAll(methods: Map<Method, EventListener>, obj: Any) {
		methods.forEach { (method, info) ->
			val eventType = method.parameterTypes[0] as Class<Event>
			val handle = lookup.unreflect(method).bindTo(obj)
			if (method.parameterCount == 1) {
				attach(Listener({ event -> handle.invoke(event) }, eventType, info.priority))
			} else {
				listenerCallback({ event, callback -> handle.invoke(event, callback) }, eventType,
					info.priority)
			}
		}
	}

	/**
	 * Enqueue [EventExecutor] to be detached from the [EventSystem]. After
	 * this method is called, there is guaranty that executor will not be
	 * called until it will be reattached to this or another [EventSystem].
	 *
	 * @param executor The [EventExecutor] to remove.
	 */
	fun detach(executor: EventExecutor) {
		eventSystem.detach(executor)
	}

	fun detachAll() {
		eventSystem.executors.values().stream().filter { isAttached(it) }
			.forEach { eventSystem.detach(it) }
	}

	companion object {
		private var count = 0
		private val lookup = MethodHandles.lookup()
	}

	@JvmOverloads
	fun <T : Event> listener(action: Consumer<T>, type: Class<T>,
							 priority: ExecutorPriority = ExecutorPriority.DEFAULT) {
		attach(Listener(action as Consumer<Event>, type as Class<Event>, priority))
	}

	@JvmOverloads
	fun <T : Event> listenerCallback(action: BiConsumer<T, EventExecutor>, type: Class<T>,
									 priority: ExecutorPriority = ExecutorPriority.DEFAULT) {
		attach(Listener(action as BiConsumer<Event, EventExecutor>, type as Class<Event>, priority))
	}

	@JvmOverloads
	fun <T : Event> task(action: Consumer<T>, type: Class<T>,
						 priority: ExecutorPriority = ExecutorPriority.DEFAULT, delay: Int = 0) {
		attach(Task(action as Consumer<Event>, type as Class<Event>, priority, delay))
	}
}

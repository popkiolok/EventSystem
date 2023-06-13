package com.nclient.event.execution

import com.nclient.event.Event
import one.util.streamex.kotlin.streamEx
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Contains a group of [EventExecutor]s with the same [EventSystem].
 * [EventExecutor]s with the same [EventContainer] can be dynamically
 * removed from the [EventSystem].
 *
 * @author NassyLove
 * @since 1.0.0
 */
@Suppress("UNCHECKED_CAST")
class EventContainer @JvmOverloads constructor(val name: String, val eventSystem: EventSystem,
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

	private fun isAttached(executor: EventExecutor<*>): Boolean {
		return executor.isAttachedTo(this) || children.stream()
			.anyMatch { child: EventContainer -> child.isAttached(executor) }
	}

	/**
	 * Attach [EventExecutor] to this event container.
	 *
	 * @param executor The [EventExecutor] to attach.
	 * @throws NullPointerException if the [executor] is null.
	 */
	fun attach(executor: EventExecutor<*>) {
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
			val eventType = (method.parameterTypes[0] as Class<Event>).kotlin
			val handle =
				lookup.unreflect(method).bindTo(obj) // TODO lamba metafactory with private method
			if (method.parameterCount == 1) {
				attach(Listener(eventType, info.priority) { event -> handle.invoke(event) })
			} else {
				attach(Listener(eventType, info.priority) { event, callback ->
					handle.invoke(event, callback)
				})
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
	fun detach(executor: EventExecutor<*>) {
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
}

/**
 * Gets [EventListener]s in the class.
 *
 * @param clazz The class to look for methods.
 * @return Annotated with [EventListener] [Method] to it annotation map.
 */
fun getEventListeners(clazz: Class<*>): Map<Method, EventListener> =
	clazz.declaredMethods.streamEx.filter {
		it.isAnnotationPresent(EventListener::class.java)
	}.toMap { m ->
		assert(m.parameterCount in 1..2) {
			"Bad EventExecutor ${m.name} in ${m.declaringClass.name} number of parameters: ${m.parameterCount}."
		}
		assert(Event::class.java.isAssignableFrom(m.parameterTypes[0])) {
			"Bad EventExecutor ${m.name} in ${m.declaringClass.name} first parameter type."
		}
		assert((m.parameterCount == 1 || EventExecutor::class.java.isAssignableFrom(
			m.parameterTypes[1]))) {
			"Bad EventExecutor ${m.name} in ${m.declaringClass.name} second parameter type."
		}
		m.isAccessible = true
		m.getAnnotation(EventListener::class.java)
	}

fun getEventListener(clazz: KClass<*>) = getEventListeners(clazz.java)

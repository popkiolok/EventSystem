package com.nclient.event.execution

import com.nclient.event.Event
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * [EventExecutor] that execute some code every time [Event] fired.
 *
 * @author NassyLove
 * @since 0.0.1
 */
class Listener<T : Event> : EventExecutor<T> {
	constructor(type: KClass<T>, priority: ExecutorPriority = ExecutorPriority.DEFAULT,
				action: (T) -> Unit) : super(type, priority, action)

	constructor(type: KClass<T>, priority: ExecutorPriority = ExecutorPriority.DEFAULT,
				action: (T, EventExecutor<T>) -> Unit) : super(type, priority, action)

	override val name: String
		get() = "Listener ${container?.name} #${hashCode()}"

	companion object {
		@JvmStatic
		fun <T : Event> listener(type: Class<T>,
								 priority: ExecutorPriority = ExecutorPriority.DEFAULT,
								 action: Consumer<T>) =
			Listener(type.kotlin, priority, action::accept)

		@JvmStatic
		fun <T : Event> listener(type: Class<T>,
								 priority: ExecutorPriority = ExecutorPriority.DEFAULT,
								 action: BiConsumer<T, EventExecutor<T>>) =
			Listener(type.kotlin, priority, action::accept)
	}
}

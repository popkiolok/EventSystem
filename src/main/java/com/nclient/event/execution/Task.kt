package com.nclient.event.execution

import com.nclient.event.Event
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * [EventExecutor] that skips [delay] event calls, executes some code when
 * [Event] type of [type] fired and detaches itself from the [EventSystem].
 *
 * @author NassyLove
 * @since 0.0.1
 */
class Task<T : Event>(type: KClass<T>, priority: ExecutorPriority = ExecutorPriority.DEFAULT,
					  private var delay: Int = 0, action: (T) -> Unit) :
	EventExecutor<T>(type, priority, action) {

	override fun accept(event: Event) {
		if (delay == 0) { // TODO: delayless task class
			super.accept(event)
			container!!.detach(this)
		} else {
			delay--
		}
	}

	override val name = "Task ${container?.name} #${hashCode()}"

	companion object {
		@JvmStatic
		fun <T : Event> task(type: Class<T>, priority: ExecutorPriority = ExecutorPriority.DEFAULT,
							 delay: Int = 0, action: Consumer<T>) =
			Task(type.kotlin, priority, delay, action::accept)
	}
}
package com.nclient.event.execution

import com.nclient.event.Event
import java.util.function.Consumer

/**
 * [EventExecutor] that skips [delay] event calls, executes some code when
 * [Event] type of [type] fired and detaches itself from the [EventSystem].
 *
 * @author NassyLove
 * @since 0.0.1
 */
class Task(action: Consumer<Event>, type: Class<out Event>,
		   priority: ExecutorPriority = ExecutorPriority.DEFAULT, private var delay: Int = 0) :
	EventExecutor(action, type, priority) {

	@Throws(EventExecutorException::class)
	override fun accept(event: Event) {
		if (delay == 0) {
			super.accept(event)
			container.detach(this)
		} else {
			delay--
		}
	}

	public override fun getName(): String {
		return String.format("Task %s #%d", container.name, hashCode())
	}
}
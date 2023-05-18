package com.nclient.event.execution

import com.nclient.event.Event
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * [EventExecutor] that execute some code every time [Event] fired.
 *
 * @author NassyLove
 * @since 0.0.1
 */
class Listener : EventExecutor {
	constructor(action: Consumer<Event>, type: Class<out Event>,
				priority: ExecutorPriority = ExecutorPriority.DEFAULT) : super(action, type,
		priority)

	internal constructor(action: BiConsumer<Event, EventExecutor>, type: Class<out Event>,
						 priority: ExecutorPriority = ExecutorPriority.DEFAULT) : super(action,
		type, priority)

	public override fun getName(): String {
		return String.format("Listener %s #%d", container.name, hashCode())
	}
}

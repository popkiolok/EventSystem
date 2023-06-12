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
class Task(action: Consumer<Event>, type: KClass<out Event>,
           priority: ExecutorPriority = ExecutorPriority.DEFAULT, private var delay: Int = 0) :
        EventExecutor(action, type.java, priority) {

    @Throws(EventExecutorException::class)
    override fun accept(event: Event) {
        if (delay == 0) { // TODO: delayless task class
            super.accept(event)
            container.detach(this)
        } else {
            delay--
        }
    }

    public override fun getName(): String {
        return String.format("Task %s #%d", container.name, hashCode())
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        @JvmOverloads
        @JvmStatic
        fun <T : Event> of(action: Consumer<T>, type: Class<T>,
                           priority: ExecutorPriority = ExecutorPriority.DEFAULT, delay: Int = 0) = Task(action as Consumer<Event>, type.kotlin, priority)
    }
}
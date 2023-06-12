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
class Listener : EventExecutor {
    constructor(action: Consumer<Event>, type: Class<out Event>,
                priority: ExecutorPriority = ExecutorPriority.DEFAULT) : super(action, type,
            priority)

    constructor(action: BiConsumer<Event, EventExecutor>, type: Class<out Event>,
                priority: ExecutorPriority = ExecutorPriority.DEFAULT) : super(action,
            type, priority)

    constructor(action: Consumer<Event>, type: KClass<out Event>,
                priority: ExecutorPriority = ExecutorPriority.DEFAULT) : this(action, type.java,
            priority)

    constructor(action: BiConsumer<Event, EventExecutor>, type: KClass<out Event>,
                priority: ExecutorPriority = ExecutorPriority.DEFAULT) : this(action,
            type.java, priority)

    public override fun getName(): String {
        return String.format("Listener %s #%d", container.name, hashCode())
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T : Event> of(action: Consumer<T>, type: Class<T>,
                           priority: ExecutorPriority = ExecutorPriority.DEFAULT) = Listener(action as Consumer<Event>, type,
                priority)

        @JvmStatic
        @JvmOverloads
        fun <T : Event> of(action: BiConsumer<T, EventExecutor>, type: Class<T>,
                           priority: ExecutorPriority = ExecutorPriority.DEFAULT) = Listener(action as BiConsumer<Event, EventExecutor>, type, priority)
    }
}

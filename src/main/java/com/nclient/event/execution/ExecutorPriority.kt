package com.nclient.event.execution

import com.nclient.event.Event

/**
 * Allows controlling executors call order. [EventExecutor]s with higher
 * [ExecutorPriority] called firstly. If the [Event] is cancelled
 * executors with not enough high priorities might not be called.
 *
 * @author NassyLove
 * @since 0.0.1
 */
enum class ExecutorPriority(val value: ULong) {
	HIGHEST(0u), HIGH(step), DEFAULT(2u * step), LOW(3u * step), LOWEST(4u * step)
}

private val step = ULong.MAX_VALUE / 5u
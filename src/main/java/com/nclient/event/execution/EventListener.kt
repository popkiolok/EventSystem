package com.nclient.event.execution

/**
 * Marks method as event listener.
 *
 * @author NassyLove
 * @since 1.0.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventListener(val priority: ExecutorPriority = ExecutorPriority.DEFAULT)

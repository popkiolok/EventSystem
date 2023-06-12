package com.nclient.event

import java.util.function.BooleanSupplier

/**
 * Cancellable event.
 *
 * @author NassyLove
 * @since 2.0.0
 */
open class Cancellable : Event() {
    override var cancelled: Boolean = false

    fun cancel() {
        cancelled = true
    }

    fun cancelIf(condition: BooleanSupplier) {
        if (condition.asBoolean) {
            cancel()
        }
    }
}

package com.nclient.event

/**
 * Contains information about event.
 *
 * @author NassyLove
 * @since 0.0.1
 */
open class Event {
    open val cancelled: Boolean
        get() = false
}

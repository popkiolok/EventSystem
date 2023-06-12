package com.nclient.event.execution

import com.google.common.collect.Iterators
import com.nclient.event.Event
import org.openjdk.jmh.annotations.*
import java.util.*
import kotlin.math.roundToInt

/**
 * @author NassyLove
 * @since 2.0.0
 */
open class EventSystemBenchmark {
    @Fork(2, warmups = 1)
    @Benchmark
    fun eventSystemWith30EventsAnd3or4ListenersForEachEventCallBenchmark(env: BenchmarkEnv) {
        env.system.call(env.events.next())
    }

    @Fork(2, warmups = 1)
    @Benchmark
    fun eventSystemCreateBenchmark() {
        EventSystem {}
    }

    @Fork(2, warmups = 1)
    @Benchmark
    fun eventSystemFirstCallBenchmark(env: BenchmarkEnv2) {
        env.system.call(BenchmarkEnv2.AnEvent())
    }

    @Fork(2, warmups = 1)
    @Benchmark
    fun eventSystemThatAlreadyContains5ListenersRegisterListenerBenchmark(env: BenchmarkEnv3) {
        env.container.attach(Listener({ _ -> }, BenchmarkEnv3.AnEvent::class))
    }

    @State(Scope.Benchmark)
    open class BenchmarkEnv {
        val system = EventSystem {}
        val container = EventContainer("BenchmarkContainer", system)
        val events: Iterator<Event>

        init {
            // Register events to the system and add listeners
            javaClass.classes.forEach {
                val numListeners = 3 + Math.random().roundToInt()
                for (i in 0 until numListeners) {
                    container.attach(Listener({ _ -> }, it.asSubclass(Event::class.java)))
                }
            }

            events = Iterators.cycle(javaClass.classes.map { it.newInstance() as Event })
            assert(system.countListeners() > 30 * 3)
        }

        class Event1 : Event()
        class Event2 : Event()
        class Event3 : Event()
        class Event4 : Event()
        class Event5 : Event()
        class Event6 : Event()
        class Event7 : Event()
        class Event8 : Event()
        class Event9 : Event()
        class Event10 : Event()
        class Event11 : Event()
        class Event12 : Event()
        class Event13 : Event()
        class Event14 : Event()
        class Event15 : Event()
        class Event16 : Event()
        class Event17 : Event()
        class Event18 : Event()
        class Event19 : Event()
        class Event20 : Event()
        class Event21 : Event()
        class Event22 : Event()
        class Event23 : Event()
        class Event24 : Event()
        class Event25 : Event()
        class Event26 : Event()
        class Event27 : Event()
        class Event28 : Event()
        class Event29 : Event()
        class Event30 : Event()
    }

    @State(Scope.Benchmark)
    open class BenchmarkEnv2 {
        lateinit var system: EventSystem

        @Setup(Level.Invocation)
        fun setup() {
            system = EventSystem {}
        }

        class AnEvent : Event()
    }

    @State(Scope.Benchmark)
    open class BenchmarkEnv3 {
        lateinit var system: EventSystem
        lateinit var container: EventContainer
        val priorities: Iterator<ExecutorPriority> = Iterators.cycle(*ExecutorPriority.values())

        @Setup(Level.Invocation)
        fun setup() {
            system = EventSystem {}
            container = EventContainer("BenchmarkContainer", system)
            for (i in 0 until 5) {
                container.attach(Listener({ _ -> }, AnEvent::class.java, priorities.next()))
            }
        }

        class AnEvent : Event()
    }
}
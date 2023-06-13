# EventSystem
Dynamic event system with multiple instances support.

## Features
<ul>
<li>Create multiple EventSystems.</li>
<li>Control listeners in EventContainer.</li>
<li>Call millions of listeners per second.</li>
<li>Create self-destroying tasks.</li>
<li>Create listeners in multiple ways.</li>
</ul>

## Adding event

```kotlin
class MyEvent(val hello: String) : Event()
class SomeCancellableEvent : Cancellable()
```

## Calling event

```kotlin
val eventSystem = EventSystem {}
// Events registered automatically on the first call
eventSystem.call(MyEvent())

val event = SomeCancellableEvent()
eventSystem.call(event)
if (event.cancelled) {
    // ...
}
```

## Listening for event

```kotlin
val container = EventContainer("MyContainer", eventSystem)
container.attach(Listener(MyEvent::class) { event -> println("MyEvent called with ${event.hello}.") })
```

```kotlin
class MyClassWithEvent {
    @EventListener
    fun printHello(event: MyEvent) {
        println("MyEvent called with ${event.hello}.")
    }
}

class Main {
    fun main() {
        container.attachAll(getEventListeners(MyClassWithEvent::class), MyClassWithEvent())
    }
}
```

## Destroying listeners

```kotlin
container.detachAll()
```

```kotlin
class SomeClassWithEvent {
    @EventListener
    fun printHello(event: MyEvent, self: EventExecutor<MyEvent>) {
        println("MyEvent called with ${event.hello}.")
        if (/* condition */) {
            self.detach()
        }
    }
}
```

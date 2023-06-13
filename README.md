# EventSystem
Dynamic event system with multiple instances support.

## Features:
<ul>
<li>Create multiple EventSystems.</li>
<li>Control listeners in EventContainer.</li>
<li>Call millions of listeners per second.</li>
<li>Create self-destroying tasks.</li>
<li>Create listeners in multiple ways.</li>
</ul>

## Adding event:

```kotlin
class SomeEvent : Event()
class SomeCancellableEvent : Cancellable()
```

## Calling event:

```kotlin
val eventSystem = EventSystem {}
// Events registered automatically on the first call
eventSystem.call(SomeEvent())

val event = SomeCancellableEvent()
eventSystem.call(event)
if (event.cancelled) {
    // ...
}
```

## Listening for event:

```kotlin
val eventSystem = EventSystem {}
val container = EventContainer("MyContainer", 
container.attach

```

# EventSystem
Dynamic event system with multiple instances support.
<br>
<h3>Features:</h3>
<ul>
<li>Create multiple EventSystems.</li>
<li>Control listeners in EventContainer.</li>
<li>Call millions of listeners per second.</li>
<li>Create self-destroying tasks.</li>
<li>Create listeners in multiple ways.</li>
</ul>
<br>
<h3>Adding event</h3>
```kotlin
    class SomeEvent : Event()
    class SomeCancellableEvent : Cancellable()
```
<h3>Calling event</h3>
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
<h3>Listening for event</h3>
```kotlin

```

<h1>N-Client Event System</h1>
Dynamic global event system.
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
<h3>Adding and calling event</h3>
```kotlin
class SomeEvent : Event()
class SomeCancellableEvent : Cancellable()
```
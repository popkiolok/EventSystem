package com.nclient.event.execution;

import com.nclient.event.Event;

import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.nclient.event.execution.EventSystem.ABSTRACT_EVENTS_SUPPORT;

/**
 * {@link EventExecutor} that can be {@link Listener} or {@link Task}.
 *
 * @author NassyLove
 * @since 1.0.0
 */
public abstract class EventExecutor {
	Consumer<Event> action;
	final Class<? extends Event> type;

	/**
	 * The {@link EventContainer} that this listener is attached to.
	 */
	protected EventContainer container;
	/**
	 * Depends on {@link ExecutorPriority}. Executors with higher priority will be called firstly.
	 */
	protected final long priority;

	/**
	 * Creates new event executor.
	 *
	 * @param action   The {@link Consumer} that will be called when event is fired.
	 * @param type     The class of the listening event. If abstract events are not supported, it
	 *                 should not be abstract.
	 * @param priority The event executor priority.
	 */
	EventExecutor(final Consumer<Event> action, final Class<? extends Event> type,
				  final ExecutorPriority priority) {
		assert ABSTRACT_EVENTS_SUPPORT || !Modifier.isAbstract(type.getModifiers()) :
				"Unable to create event listener with abstract event type.";
		this.action = action;
		this.type = type;
		this.priority = priority.getValue() + EventSystem.nextExecutorId++;
	}

	EventExecutor(final BiConsumer<Event, EventExecutor> action, final Class<? extends Event> type,
				  final ExecutorPriority priority) {
		assert ABSTRACT_EVENTS_SUPPORT || !Modifier.isAbstract(type.getModifiers()) :
				"Unable to create event listener with abstract event type.";
		this.action = event -> action.accept(event, this);
		this.type = type;
		this.priority = priority.getValue() + EventSystem.nextExecutorId++;
	}

	public void accept(final Event event) throws EventExecutorException {
		try {
			action.accept(type.cast(event));
		} catch (final Throwable e) {
			throw new EventExecutorException(getName(), e);
		}
	}

	final long getPriority() {
		return priority;
	}

	abstract String getName();

	/**
	 * Attach event executor to specified {@link EventContainer}.
	 *
	 * @param container The {@link EventContainer} to attach to.
	 */
	final void attachTo(final EventContainer container) {
		assert this.container == null :
				"Reattaching or double attaching the same executor is not supported.";
		this.container = container;
	}

	/**
	 * Gets is this event executor is attached to specified {@link EventContainer}.
	 *
	 * @param container The {@link EventContainer} to check for.
	 *
	 * @return True if this event executor is attached to specified {@link EventContainer} false
	 * otherwise.
	 */
	final boolean isAttachedTo(final EventContainer container) {
		return this.container == container;
	}

	public final void detach() {
		container.detach(this);
	}
}

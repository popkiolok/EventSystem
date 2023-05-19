package com.nclient.event.execution;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.nclient.event.Event;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Allows handling and fire events.
 *
 * @author NassyLove
 * @since 1.0.0
 */
public class EventSystem {
	static boolean ABSTRACT_EVENTS_SUPPORT;
	static long nextExecutorId;

	final SetMultimap<Class<? extends Event>, EventExecutor> executors = MultimapBuilder.hashKeys()
			.treeSetValues(Comparator.comparingLong(EventExecutor::getPriority))
			.build();

	private final Queue<EventExecutor> toRemove = new LinkedList<>();
	private final Consumer<EventExecutorException> errorAction;
	private int listeners;
	private int tasks;

	public EventSystem(final Consumer<EventExecutorException> errorAction) {
		this.errorAction = errorAction;
	}

	/**
	 * Call this {@link Event} event listeners.
	 *
	 * @param event The {@link Event} to call.
	 *
	 * @return true if event is canceled, false otherwise.
	 */
	public boolean call(final Event event) {
		synchronized (toRemove) {
			while (!toRemove.isEmpty()) {
				final EventExecutor executor = toRemove.poll();
				executors.remove(executor.type, executor);
			}
		}

		for (final EventExecutor executor : executors.get(event.getClass())) {
			try {
				executor.accept(event);
				if (event.isCancelled()) {
					return true;
				}
			} catch (final EventExecutorException e) {
				errorAction.accept(e);
			}
		}
		return false;
	}

	/**
	 * Gets the number of listeners currently attached to this event manager.
	 *
	 * @return The number of listeners currently attached to this event manager.
	 */
	public int countListeners() {
		return listeners;
	}

	/**
	 * Gets the number of tasks currently attached to this event manager.
	 *
	 * @return The number of tasks currently attached to this event manager.
	 */
	public int countTasks() {
		return tasks;
	}

	/**
	 * Attach {@link EventExecutor} to this {@link EventSystem}.
	 *
	 * @param executor The {@link EventExecutor} to attach.
	 */
	void attach(final EventExecutor executor) {
		executors.put(executor.type, executor);
		if (executor instanceof Listener) {
			listeners++;
		} else if (executor instanceof Task) {
			tasks++;
		}
	}

	/**
	 * Enqueue {@link EventExecutor} to be detached from this {@link EventSystem}. After this method
	 * is called, there is guaranty that executor will not be called until it will be reattached to
	 * this or another {@link EventSystem}.
	 *
	 * @param executor The {@link EventExecutor} to remove.
	 */
	void detach(final EventExecutor executor) {
		toRemove.offer(executor);
		if (executor instanceof Listener) {
			listeners--;
		} else if (executor instanceof Task) {
			tasks--;
		}
	}

	/**
	 * Allows {@link EventSystem}s to support abstract {@link Event}s. When {@link EventExecutor}
	 * listening abstract event, it will be called when any of abstract event inheritors are called.
	 * By default, abstract events are not supported.
	 *
	 * @param abstractEventsSupport The value to set.
	 */
	public static void setAbstractEventsSupport(final boolean abstractEventsSupport) {
		ABSTRACT_EVENTS_SUPPORT = abstractEventsSupport;
	}
}
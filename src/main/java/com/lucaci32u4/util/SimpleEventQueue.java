package com.lucaci32u4.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

public class SimpleEventQueue<Event> {
	interface CheckEvent<Event> {
		boolean test(@NotNull Event e);
	}
	
	private ArrayDeque<Event> queue;
	private Semaphore lock;
	private JSignal newEvent;
	
	public SimpleEventQueue() {
		queue = new ArrayDeque<>();
		lock = new Semaphore(0);
		newEvent = new JSignal(false);
		lock.release();
	}
	
	public void produce(@NotNull Event event) {
		lock.acquireUninterruptibly();
		queue.offer(event);
		lock.release();
		newEvent.set(true);
	}
	
	public Event consume(boolean wait) {
		return consumeIf(wait, (e) -> (true));
	}
	public Event consumeIf(boolean wait, @NotNull CheckEvent check) {
		Event result = null;
		newEvent.set(false);
		result = atomicIterateWithCheck(check);
		while (wait && result == null) {
			newEvent.waitFor(true);
			result = atomicIterateWithCheck(check);
		}
		return result;
	}
	
	private Event atomicIterateWithCheck(@NotNull CheckEvent check) {
		Event result = null;
		lock.acquireUninterruptibly();
		for (Event event : queue) {
			if (check.test(event)) {
				result = event;
			}
		}
		queue.remove(result);
		lock.release();
		return result;
	}
}

/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

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

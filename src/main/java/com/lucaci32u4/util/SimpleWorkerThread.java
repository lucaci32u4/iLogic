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

public class SimpleWorkerThread {

	private Thread thread;
	private JSignal submit;
	private JSignal finish;
	private Runnable task;
	private volatile boolean running;

	public SimpleWorkerThread(Runnable workerTask) {
		task = workerTask;
		submit = new JSignal(false);
		finish = new JSignal(false);
		running = true;
		thread = new Thread(() -> {
			while (running) {
				if (!submit.get()) finish.set(true);
				submit.waitFor(true);
				submit.set(false);
				finish.set(false);
				if (running) task.run();
				else break;
			}
		});
	}

	public void start() {
		thread.start();
	}

	public void submit() {
		submit.set(true);
	}
	
	public boolean finish(boolean wait) {
		boolean b = finish.get();
		if (!b && wait) {
			finish.waitFor(true);
			b = true;
		}
		return b;
	}

	public void exit(boolean join) {
		running = false;
		submit.set(true);
		if (join) {
			Helper.join(thread);
		}
	}
}

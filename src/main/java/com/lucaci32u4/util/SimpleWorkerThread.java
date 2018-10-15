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

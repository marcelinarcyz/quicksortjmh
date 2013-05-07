package org.sortbench.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;

public class NoMutexThreadPool {
	public NoMutexThreadPool(int numThreads) {
		threads = new Thread[numThreads];

		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(new SimpleWorker());
		}

        for (Thread t : threads) {
            t.start();
        }
	}

	public void submitTask(Runnable task) {
		tasks.push(task);
	}

	public void shutdown() throws InterruptedException {
        shuttingDown.set(true);

        for (Thread thread : threads) {
			thread.join();
		}
	}

	public final Thread[] threads;
	public final AtomicBoolean shuttingDown = new AtomicBoolean();
	public final AtomicStack<Runnable> tasks = new AtomicStack<Runnable>();

	public class SimpleWorker implements Runnable {
		@Override
		public void run() {
			while (shuttingDown.get() == false) {
				Runnable task = tasks.pop();
				if (task == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				} else {
					task.run();
				}
			}
		}
	}
}

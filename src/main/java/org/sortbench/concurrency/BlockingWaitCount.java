package org.sortbench.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockingWaitCount {
	public void increment() {
		waitCount.incrementAndGet();
	}

	public void decrement() {
		int updatedValue = waitCount.decrementAndGet();

		if (updatedValue == 0) {
			synchronized (mutex) {
				mutex.notifyAll();
			}
		}
	}

	public void waitOnZero() throws InterruptedException {
		synchronized (mutex) {
			while (waitCount.get() > 0) {
				mutex.wait();
			}
		}
	}

	public final Object mutex = new Object();
	// Using an atomic integer as opposed to a plain int to reduce synchronized blocks.
	public final AtomicInteger waitCount = new AtomicInteger();
}

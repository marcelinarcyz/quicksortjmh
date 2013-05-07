package org.sortbench.quicksort;

import org.sortbench.concurrency.BlockingWaitCount;
import org.sortbench.concurrency.NoMutexThreadPool;

public class QuickSortSimpleThreadPool {
	public static void sort(int[] a, int parallelizationCount) throws InterruptedException {
        NoMutexThreadPool threadPool = new NoMutexThreadPool(parallelizationCount);
		QuickSortSimpleThreadPool instance = new QuickSortSimpleThreadPool(a, threadPool);
		instance.sort();
        threadPool.shutdown();
	}

    public static void sort(int[] a, NoMutexThreadPool threadPool) throws InterruptedException {
        QuickSortSimpleThreadPool instance = new QuickSortSimpleThreadPool(a, threadPool);
        instance.sort();
    }

	// Implementation...

	protected final NoMutexThreadPool threadPool;
	protected final BlockingWaitCount waitCount = new BlockingWaitCount();
	protected final int[] a;

	protected QuickSortSimpleThreadPool(int[] a, NoMutexThreadPool threadPool) {
		this.threadPool = threadPool;
		this.a = a;
	}

	protected void sort() throws InterruptedException {
		waitCount.increment();
		threadPool.submitTask(new QuickSortTask(0, a.length));
		waitCount.waitOnZero();
	}

	public class QuickSortTask implements Runnable {
		protected int i0;   // Inclusive
		protected int i1;   // Exclusive

		public QuickSortTask(int i0, int i1) {
			this.i0 = i0;
			this.i1 = i1;
		}

		@Override
		public void run() {
			runInternal();

			waitCount.decrement();
		}

		public void runInternal() {
			boolean continueIteration = true;
			while (continueIteration) {
				int length = i1 - i0;

				if (length < 2) {
					return;
				} else if (length == 2) {
					if (a[i1 - 1] < a[i0]) {
						int t = a[i0];
						a[i0] = a[i1 - 1];
						a[i1 - 1] = t;
					}
					return;
				}

				int swapIndex = i0 + 1;
				int pivotValue = a[i0];
				for (int i = i0 + 1; i < i1; i++) {
					int v = a[i];
					if (v <= pivotValue) {
                        a[i] = a[swapIndex];
                        a[swapIndex] = v;

						swapIndex++;
					}
				}

				int newPivotIndex = swapIndex - 1;
				int t = a[newPivotIndex];
				a[newPivotIndex] = a[i0];
				a[i0] = t;

                int currenti1 = i1;
				continueIteration = false;

				if (newPivotIndex - i0 > 1) {
                    i1 = newPivotIndex;
                    continueIteration = true;
				}
				if ((currenti1 - swapIndex) > 1) {
					if (continueIteration) {
						waitCount.increment();
						threadPool.submitTask(new QuickSortTask(swapIndex, currenti1));
					} else {
						i0 = newPivotIndex + 1;
						continueIteration = true;
					}
				}
			}
		}
	}
}


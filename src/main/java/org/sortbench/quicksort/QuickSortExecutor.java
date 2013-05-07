package org.sortbench.quicksort;

import org.sortbench.concurrency.BlockingWaitCount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuickSortExecutor {
	public static void sort(int[] a, int parallelizationCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(parallelizationCount);
		QuickSortExecutor instance = new QuickSortExecutor(a, executorService);
		instance.sort();
        executorService.shutdown();
	}

    public static void sort(int[] a, ExecutorService executorService) throws InterruptedException {
        QuickSortExecutor instance = new QuickSortExecutor(a, executorService);
        instance.sort();
    }

	// Implementation...

	protected final ExecutorService executorService;
	protected final BlockingWaitCount waitCount = new BlockingWaitCount();
	protected final int[] a;

	protected QuickSortExecutor(int[] a, ExecutorService executorService) {
		this.executorService = executorService;
		this.a = a;
	}

	protected void sort() throws InterruptedException {
		waitCount.increment();
		executorService.submit(new QuickSortTask(0, a.length));
		waitCount.waitOnZero();
	}

	public class QuickSortTask implements Runnable {
		protected int i0;
		protected int i1;

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
                        int t = a[swapIndex];
                        a[swapIndex] = a[i];
                        a[i] = t;
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
                        executorService.submit(new QuickSortTask(swapIndex, currenti1));
                    } else {
                        i0 = newPivotIndex + 1;
                        continueIteration = true;
                    }
                }
            }
        }
	}
}

package org.sortbench.quicksort;

import org.sortbench.concurrency.BlockingWaitCount;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuickSortForkJoin {
	static final Logger logger = Logger.getLogger(QuickSortForkJoin.class.getName());

	public static void sort(int[] a, int parallelizationCount) throws InterruptedException {
        ForkJoinPool fjPool = new ForkJoinPool(parallelizationCount);
		QuickSortForkJoin instance = new QuickSortForkJoin(a, fjPool);
		instance.sort();
        fjPool.shutdown();
	}

    public static void sort(int[] a, ForkJoinPool fjPool) throws InterruptedException {
        QuickSortForkJoin instance = new QuickSortForkJoin(a, fjPool);
        instance.sort();
    }

	// Implementation...

	//protected final int numProcessors = Runtime.getRuntime().availableProcessors();
	protected final ForkJoinPool fjPool;
	protected final int[] a;
	protected final BlockingWaitCount waitCount = new BlockingWaitCount();
	//protected final AtomicInteger debugBeginEndCount = new AtomicInteger(0);
	//protected final AtomicInteger debugForkJoinCount = new AtomicInteger(0);

	protected QuickSortForkJoin(int[] a, ForkJoinPool fjPool) {
		this.fjPool = fjPool;
		this.a = a;
	}

	protected void sort() throws InterruptedException {
		//logger.info(String.format("Starting FJ sort on %d elements with %d processors", a.length, fjPool.getParallelism()));
		
        waitCount.increment();
		fjPool.invoke(new QuickSortTask(0, a.length));
		waitCount.waitOnZero();
	}

	public class QuickSortTask extends RecursiveAction {
		protected int i0;
		protected int i1;

		public QuickSortTask(int i0, int i1) {
			this.i0 = i0;
			this.i1 = i1;
		}

		@Override
		protected void compute() {
			try {
				//logger.info(String.format("Starting. %d", debugBeginEndCount.incrementAndGet()));				
				computeInternal();
				waitCount.decrement();
				//logger.info(String.format("Completing. %d", debugBeginEndCount.decrementAndGet()));				
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "Exception in sort code", e);
			}
		}
		protected void computeInternal() {
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
					this.i1 = newPivotIndex;
					continueIteration = true;
				}
				if ((currenti1 - swapIndex) > 1) {
					if (continueIteration) {
                        waitCount.increment();
						QuickSortTask task = new QuickSortTask(swapIndex, currenti1);
						task.fork();
					} else {
						this.i0 = newPivotIndex + 1;
						continueIteration = true;
					}
				}
			}
		}
	}
}

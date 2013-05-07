package org.sortbench.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.sortbench.concurrency.NoMutexThreadPool;
import org.sortbench.quicksort.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@SuppressWarnings({"UnusedDeclaration"})
@OutputTimeUnit(TimeUnit.SECONDS)
public class Benchmark {
    protected int parallelizationCount = 8;
    protected int size = 10000000;
    protected boolean validateSort = false;

    protected int[] array;
    protected NoMutexThreadPool threadPool;
    protected ExecutorService executorService;
    protected ForkJoinPool fjPool;

    @Setup
    public void setup() {
        array = ArrayUtilities.generateUnsorted(size);

        threadPool = new NoMutexThreadPool(parallelizationCount);
        executorService = Executors.newFixedThreadPool(parallelizationCount);
        fjPool = new ForkJoinPool(parallelizationCount);
    }

    @TearDown
    public void tearDown() throws InterruptedException {
        threadPool.shutdown();
        executorService.shutdown();
        fjPool.shutdown();
    }

    @GenerateMicroBenchmark
    public void singleThreadedRecursiveSort() {
        int[] a = array.clone();
        QuickSortRecursive.sort(a);

        if (validateSort) {
            ArrayUtilities.assertSorted(a);
        }
    }

    @GenerateMicroBenchmark
    public void singleThreadedSerialSort() {
        int[] a = array.clone();
        QuickSortSerial.sort(a);

        if (validateSort) {
            ArrayUtilities.assertSorted(a);
        }
    }

    @GenerateMicroBenchmark
    public void executorServiceSort() throws InterruptedException {
        int[] a = array.clone();
        QuickSortExecutor.sort(a, executorService);

        if (validateSort) {
            ArrayUtilities.assertSorted(a);
        }
    }

    @GenerateMicroBenchmark
    public void customThreadPoolSort() throws InterruptedException {
        int[] a = array.clone();
        QuickSortSimpleThreadPool.sort(a, threadPool);

        if (validateSort) {
            ArrayUtilities.assertSorted(a);
        }
    }

    @GenerateMicroBenchmark
    public void forkJoinSort() throws InterruptedException {
        int[] a = array.clone();
        QuickSortForkJoin.sort(a, fjPool);

        if (validateSort) {
            ArrayUtilities.assertSorted(a);
        }
    }
}

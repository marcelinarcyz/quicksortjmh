quicksortjmh
============

This is a quicksort macrobenchmark using Oracle's jmh benchmarking tool. This benchmarks a simple quicksort algorithm using five different strategies:

- Single-threaded recursive
- Single-threaded using a local task stack to avoid explicit recursion
- Multi-threaded using the Java executor thread pool system.
- Multi-threaded using a custom lock-free thread pool system.
- Multi-threaded using Java 7 Fork Join

Output of a run. This is with a sort size of 10,000,000. Higher ops/sec is faster:

```
Benchmark                                         Thr    Cnt  Sec         Mean   Mean error          Var    Units
o.s.b.g.t.Benchmark.customThreadPoolSort            1      5   20        1.150        0.000        0.000  ops/sec
o.s.b.g.t.Benchmark.executorServiceSort             1      5   20        0.370        0.015        0.000  ops/sec
o.s.b.g.t.Benchmark.forkJoinSort                    1      5   20        2.053        0.066        0.001  ops/sec
o.s.b.g.t.Benchmark.singleThreadedRecursiveSort     1      5   20        0.720        0.015        0.000  ops/sec
o.s.b.g.t.Benchmark.singleThreadedSerialSort        1      5   20        0.650        0.042        0.000  ops/sec
```

To run this, you will need

- JDK 7+
- [Gradle](http://gradle.org)
- [jmh](http://openjdk.java.net/projects/code-tools/jmh/)

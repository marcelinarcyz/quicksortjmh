#!/bin/sh
java -jar build/dist/jmhsort.jar -v -wi 2 -i 5 -r 20s ".*Benchmark.*"

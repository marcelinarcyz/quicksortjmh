package org.sortbench.benchmarks;

import java.util.Random;

public class ArrayUtilities {
    public static void assertSorted(int[] a) {
        if (a.length < 1) {
            return;
        }
        int last = a[0];
        for (int i = 1; i < a.length; i++) {
            int current = a[i];
            assert (last <= current);
            if (last > current) {
                throw new RuntimeException("Not sorted");
            }
            last = current;
        }
    }

    public static int[] generateUnsorted(int size) {
        Random g = new Random();

        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = g.nextInt();
        }

        return a;
    }
}

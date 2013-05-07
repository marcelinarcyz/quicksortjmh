package org.sortbench.quicksort;

import org.sortbench.concurrency.PersistentImmutableList;

public class QuickSortSerial {
    public static void sort(int[] a) {
        PersistentImmutableList<IntegerRange> rangesToSort = PersistentImmutableList.<IntegerRange>emptyList();
        rangesToSort = rangesToSort.prepend(new IntegerRange(0, a.length));

        while (!rangesToSort.empty()) {
            final IntegerRange rangeToSort = rangesToSort.head();
            rangesToSort = rangesToSort.tail();

            final int i0 = rangeToSort.lowerBoundInclusive;
            final int i1 = rangeToSort.upperBoundExclusive;

            int length = i1 - i0;

            if (length < 2) {
                // Skip.
            } else if (length == 2) {
                if (a[i1 - 1] < a[i0]) {
                    int t = a[i0];
                    a[i0] = a[i1 - 1];
                    a[i1 - 1] = t;
                }
            } else {
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

                if (newPivotIndex - i0 > 1) {
                    rangesToSort = rangesToSort.prepend(new IntegerRange(i0, newPivotIndex));
                }
                if (i1 - (newPivotIndex + 1) > 1) {
                    rangesToSort = rangesToSort.prepend(new IntegerRange(newPivotIndex + 1, i1));
                }
            }
        }
    }

    static class IntegerRange {
        public IntegerRange(int lowerBoundInclusive, int upperBoundExclusive) {
            this.lowerBoundInclusive = lowerBoundInclusive;
            this.upperBoundExclusive = upperBoundExclusive;
        }
        public final int lowerBoundInclusive;
        public final int upperBoundExclusive;
    }
}

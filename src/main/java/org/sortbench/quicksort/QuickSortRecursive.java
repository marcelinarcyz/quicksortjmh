package org.sortbench.quicksort;

public class QuickSortRecursive {
	public static void sort(int[] a) {
		quickSort(a, 0, a.length);
	}

	// Sorts interval: [i0, i1)
	public static void quickSort(int[] a, int i0, int i1) {
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

		if (newPivotIndex - i0 > 1) {
			quickSort(a, i0, newPivotIndex);
		}
		if (i1 - (newPivotIndex + 1) > 1) {
			quickSort(a, newPivotIndex + 1, i1);
		}
	}
}

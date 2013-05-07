package org.sortbench.concurrency;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicStack<T> {
	protected static class StackNode<T> {
		public final T value;
		public final StackNode<T> next;

		public StackNode(T value, StackNode<T> next) {
			this.value = value;
            this.next = next;
		}
	}

	protected final AtomicReference<StackNode<T>> head = new AtomicReference<StackNode<T>>();

	public AtomicStack() {

	}

	public boolean empty() {
		return head.get() == null;
	}

	public void push(T value) {
		while (true) {
			StackNode<T> currentHead = head.get();
            StackNode<T> newHead = new StackNode<T>(value, currentHead);

			if (head.compareAndSet(currentHead, newHead)) {
				break;
			}
		}
	}

	public T pop() {
		while (true) {
			StackNode<T> headToPop = head.get();
			if (headToPop == null) {
				return null;
			}
			if (head.compareAndSet(headToPop, headToPop.next)) {
				return headToPop.value;
			}
		}
	}
}

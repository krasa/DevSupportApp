package krasa.core.backend.common;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * If you use this code, please retain this comment block.
 * 
 * @author Isak du Preez isak at du-preez dot com www.du-preez.com
 */
public class ArrayListDeque<E> extends AbstractList<E> implements RandomAccess {

	private final int bufferLength; // buffer length
	private final List<E> buf; // a List implementing RandomAccess
	private int head = 0;
	private int tail = 0;

	public ArrayListDeque(int capacity) {
		bufferLength = capacity + 1;
		buf = new ArrayList<>(Collections.nCopies(bufferLength, (E) null));
	}

	public int capacity() {
		return bufferLength - 1;
	}

	private int wrapIndex(int i) {
		int m = i % bufferLength;
		if (m < 0) { // java modulus can be negative
			m += bufferLength;
		}
		return m;
	}

	// This method is O(n) but will never be called if the
	// CircularArrayList is used in its typical/intended role.
	private void shiftBlock(int startIndex, int endIndex) {
		assert (endIndex > startIndex);
		for (int i = endIndex - 1; i >= startIndex; i--) {
			set(i + 1, get(i));
		}
	}

	public E removeOldest() {
		E set = buf.set(head, null);
		head = wrapIndex(++head);
		return set;
	}

	@Override
	public int size() {
		if (tail < head) {
			return bufferLength - (head - tail);
		} else {
			return tail - head;

		}
	}

	@Override
	public E get(int i) {
		if (i < 0 || i >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return buf.get(wrapIndex(head + i));
	}

	public void insert(E e) {
		int s = size();
		if (s == bufferLength - 1) {
			throw new IllegalStateException("Cannot add element." + " CircularArrayList is filled to capacity.");
		}
		buf.set(tail, e);
		tail = wrapIndex(tail + 1);
	}

	@Override
	public E remove(int i) {
		int s = size();
		if (i < 0 || i >= s) {
			throw new IndexOutOfBoundsException();
		}
		E e = get(i);
		if (i > 0) {
			shiftBlock(0, i);
		}
		head = wrapIndex(head + 1);
		return e;
	}
}

package hs.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTree<T> implements Tree<T> {

	@Override
	public int size() {
		int c = 0;
		Iterator<Node<T>> it = iterator();
		while (it.hasNext()) {
			it.next();
			c++;
		}
		return c;
	}

	@Override
	public void clear() {
		Iterator<Node<T>> it = iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	@Override
	public boolean isEmpty() {
		return iterator().hasNext();
	}

	@Override
	public Node<T> getRoot() {
		Iterator<Node<T>> it = iterator();
		return (it.hasNext()) ? it.next() : null;
	}

	@Override
	public Node<T> getParent(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		for (Node<T> tn : this) {
			Iterator<Node<T>> children = childrenIt(tn);
			while (children.hasNext()) {
				Node<T> child = children.next();
				if (child.equals(n)) {
					return tn;
				}
			}
		}
		return null;
	}

	@Override
	public int getGrade(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		int c = 0;
		Iterator<Node<T>> children = childrenIt(n);
		while (children.hasNext()) {
			children.next();
			c++;
		}
		return c;
	}

	@Override
	public List<Node<T>> getChildren(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		LinkedList<Node<T>> children = new LinkedList<>();
		Iterator<Node<T>> it = childrenIt(n);
		while (it.hasNext()) {
			Node<T> child = it.next();
			children.addLast(child);
		}
		return children;
	}

	@Override
	public boolean contains(Node<T> n) {
		if (n == null) {
			return false;
		}
		for (Node<T> v : this) {
			if (n.equals(v)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tree)) {
			return false;
		}
		if (o == this) {
			return true;
		}
		Tree<T> tr = (Tree<T>) o;
		if (size() != tr.size()) {
			return false;
		}
		Iterator<Node<T>> it1 = iterator();
		Iterator<Node<T>> it2 = tr.iterator();
		while (it1.hasNext()) {
			Node<T> n1 = it1.next();
			Node<T> n2 = it2.next();
			if (!n1.equals(n2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final int MULT = 29;
		Iterator<Node<T>> it = iterator();
		while (it.hasNext()) {
			Node<T> n = it.next();
			h = h * MULT + n.hashCode();
		}
		return h;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(200);
		sb.append('[');
		Iterator<Node<T>> it = iterator();
		while (it.hasNext()) {
			sb.append(it.next().toString());
			if (it.hasNext()) {
				sb.append(',');
				sb.append(' ');
			}
		}
		sb.append(']');
		return sb.toString();
	}

}

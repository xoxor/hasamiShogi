package hs.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TreeFSNB<T> extends AbstractTree<T> {

	private NodeFSNB<T> root = null;
	private int size = 0;

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public NodeFSNB<T> getRoot() {
		return root;
	}

	// pre: this.contains(n) == true
	@Override
	public NodeFSNB<T> getParent(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		if (!(n instanceof NodeFSNB)) {
			throw new DifferentNodeTypesException();
		}
		return ((NodeFSNB<T>) n).getParent();
	}

	// pre: this.contains(n) == true
	@Override
	public int getGrade(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		if (!(n instanceof NodeFSNB)) {
			throw new DifferentNodeTypesException();
		}
		int c = 0;
		NodeFSNB<T> v = ((NodeFSNB<T>) n).getFirst();
		while (v != null) {
			c++;
			v = v.getNext();
		}
		return c;
	}

	@Override
	public NodeFSNB<T> addRoot(T info) {
		if (root != null) {
			throw new ExistingNodeException();
		}
		root = new NodeFSNB<T>(info);
		size++;
		return root;
	}

	// pre: this.contains(n) == true
	@Override
	public NodeFSNB<T> addChild(Node<T> parent, T info) {
		if (parent == null) {
			throw new NullPointerException();
		}
		if (!(parent instanceof NodeFSNB)) {
			throw new DifferentNodeTypesException();
		}
		NodeFSNB<T> n = (NodeFSNB<T>) parent;
		NodeFSNB<T> child = new NodeFSNB<T>(info);
		child.setNext(n.getFirst());
		n.setFirst(child);
		child.setParent(n);
		size++;
		return child;
	}

	// pre: this.contains(n) == true
	@Override
	public Iterator<Node<T>> childrenIt(Node<T> n) {
		if (n == null) {
			throw new NullPointerException();
		}
		if (!(n instanceof NodeFSNB)) {
			throw new DifferentNodeTypesException();
		}
		return new ChildrenIt((NodeFSNB<T>) n);
	}

	@Override
	public Iterator<Node<T>> iterator() {
		// TODO Auto-generated method stub
		return breadthFirstSearchIt();
	}

	@Override
	public Iterator<Node<T>> depthFirstSearchIt(VisitPattern vp) {
		switch (vp) {
		case PRE_ORDER:
			return new PreOrderIt();
		case IN_ORDER:
			throw new UnsupportedOperationException();
		case POST_ORDER:
			return new PostOrderIt();
		default:
			return new PreOrderIt();
		}
	}

	@Override
	public Iterator<Node<T>> depthFirstSearchIt() {
		return new PreOrderIt();
	}

	@Override
	public Iterator<Node<T>> breadthFirstSearchIt() {
		return new BreadthFirstIt();
	}

	private class ChildrenIt implements Iterator<Node<T>> {

		private NodeFSNB<T> next = null;

		public ChildrenIt(NodeFSNB<T> n) {
			next = n.getFirst();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public NodeFSNB<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			NodeFSNB<T> n = next;
			next = next.getNext();
			return n;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private class PreOrderIt implements Iterator<Node<T>> {

		private LinkedList<NodeFSNB<T>> stack;

		public PreOrderIt() {
			stack = new LinkedList<NodeFSNB<T>>();
			if (root != null) {
				stack.push(root);
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public NodeFSNB<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			NodeFSNB<T> n = stack.pop();
			if (n.getNext() != null) {
				stack.push(n.getNext());
			}
			if (n.getFirst() != null) {
				stack.push(n.getFirst());
			}
			return n;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private class PostOrderIt implements Iterator<Node<T>> {

		private LinkedList<NodeFSNB<T>> stack;

		public PostOrderIt() {
			stack = new LinkedList<NodeFSNB<T>>();
			NodeFSNB<T> cursor = root;
			while (cursor != null) {
				stack.push(cursor);
				cursor = cursor.getFirst();
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public NodeFSNB<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			NodeFSNB<T> n = stack.pop();
			NodeFSNB<T> cursor = n.getNext();
			while (cursor != null) {
				stack.push(cursor);
				cursor = cursor.getFirst();
			}
			return n;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class BreadthFirstIt implements Iterator<Node<T>> {

		private LinkedList<NodeFSNB<T>> next;

		public BreadthFirstIt() {
			next = new LinkedList<NodeFSNB<T>>();
			if (root != null) {
				next.addLast(root);
			}
		}

		@Override
		public boolean hasNext() {
			return !next.isEmpty();
		}

		@Override
		public NodeFSNB<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			NodeFSNB<T> n = next.removeFirst();
			NodeFSNB<T> cursor = n.getFirst();
			while (cursor != null) {
				next.addLast(cursor);
				cursor = cursor.getNext();
			}
			return n;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}

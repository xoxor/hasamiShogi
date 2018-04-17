package hs.util;

import java.util.Iterator;
import java.util.List;

public interface Tree<T> extends Iterable<Node<T>> {

	public enum VisitPattern {
		PRE_ORDER, IN_ORDER, POST_ORDER
	}

	int size();

	void clear();

	boolean isEmpty();

	Node<T> getRoot();

	Node<T> getParent(Node<T> n);

	int getGrade(Node<T> n);

	List<Node<T>> getChildren(Node<T> n);

	Iterator<Node<T>> childrenIt(Node<T> n);

	boolean contains(Node<T> n);

	Node<T> addRoot(T info);

	Node<T> addChild(Node<T> parent, T info);

	Iterator<Node<T>> depthFirstSearchIt();

	Iterator<Node<T>> breadthFirstSearchIt();

	Iterator<Node<T>> depthFirstSearchIt(VisitPattern vp);
}

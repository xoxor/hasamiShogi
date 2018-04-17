package hs.util;

public abstract class Node<T> {

	private T info;

	public Node(T info) {
		this.info = info;
	}

	public T getInfo() {
		return info;
	}

	public void setInfo(T info) {
		this.info = info;
	}

	public String toString() {
		return info.toString();
	}

}

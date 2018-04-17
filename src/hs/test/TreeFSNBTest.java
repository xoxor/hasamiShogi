package hs.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hs.util.Node;
import hs.util.NodeFSNB;
import hs.util.Tree.VisitPattern;
import hs.util.TreeFSNB;

public class TreeFSNBTest {

	private TreeFSNB<Integer> tree;

	@Before
	public void runOnceBeforeEachTest() {
		tree = new TreeFSNB<>();
	}

	@After
	public void runOnceAfterEachTest() {
		tree = null;
	}

	@Test
	public void testSize() {
		assertEquals(0, tree.size());
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		assertEquals(1, tree.size());
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);
		assertEquals(4, tree.size());
	}

	@Test
	public void testClear() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);
		assertEquals(4, tree.size());
		tree.clear();
		assertEquals(0, tree.size());
	}

	@Test
	public void testIsEmpty() {
		assertThat(tree.isEmpty(), is(true));
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		assertThat(tree.isEmpty(), is(false));
		tree.clear();
		assertThat(tree.isEmpty(), is(true));
	}

	@Test
	public void testGetRoot() {
		assertEquals(null, tree.getRoot());
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		assertEquals(n0, tree.getRoot());
	}

	@Test
	public void testGetParent() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);
		assertEquals(null, tree.getParent(n0));
		assertEquals(n0, tree.getParent(n1));
		assertEquals(n0, tree.getParent(n2));
		assertEquals(n1, tree.getParent(n3));
	}

	@Test
	public void getGrade() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);
		assertThat(tree.getGrade(n0), is(2));
		assertThat(tree.getGrade(n1), is(1));
		assertThat(tree.getGrade(n2), is(0));
		assertThat(tree.getGrade(n3), is(0));
	}

	@Test
	public void testGetChildren() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		List<Node<Integer>> children = new LinkedList<>();
		((LinkedList<Node<Integer>>) children).addLast(n2);
		((LinkedList<Node<Integer>>) children).addLast(n1);
		assertThat(tree.getChildren(n0), is(children));

		children.clear();
		((LinkedList<Node<Integer>>) children).addLast(n3);
		assertThat(tree.getChildren(n1), is(children));

		children.clear();
		assertThat(tree.getChildren(n2), is(children));
		assertThat(tree.getChildren(n3), is(children));
	}

	@Test
	public void testGetChildrenIt() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		Iterator<Node<Integer>> it = tree.childrenIt(n0);
		Iterator<Node<Integer>> children = tree.getChildren(n0).iterator();
		while (it.hasNext()) {
			assertThat(children.hasNext(), is(true));
			assertThat(it.next().equals(children.next()), is(true));
		}
		assertThat(children.hasNext(), is(false));

		it = tree.childrenIt(n1);
		children = tree.getChildren(n1).iterator();
		while (it.hasNext()) {
			assertThat(children.hasNext(), is(true));
			assertThat(it.next().equals(children.next()), is(true));
		}
		assertThat(children.hasNext(), is(false));

		it = tree.childrenIt(n2);
		children = tree.getChildren(n2).iterator();
		while (it.hasNext()) {
			assertThat(children.hasNext(), is(true));
			assertThat(it.next().equals(children.next()), is(true));
		}
		assertThat(children.hasNext(), is(false));

		it = tree.childrenIt(n3);
		children = tree.getChildren(n3).iterator();
		while (it.hasNext()) {
			assertThat(children.hasNext(), is(true));
			assertThat(it.next().equals(children.next()), is(true));
		}
		assertThat(children.hasNext(), is(false));
	}

	@Test
	public void testContains() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		assertThat(tree.contains(n0), is(true));
	}

	@Test
	public void testAddRoot() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		assertThat(tree.getRoot(), is(n0));
	}

	@Test
	public void testAddChild() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		assertThat(tree.contains(n1), is(true));
		assertThat(n1.getInfo(), is(1));
		assertThat(n1.getParent(), is(n0));
		assertThat(n1.getFirst(), is(n3));
		assertThat(n2.getNext(), is(n1));
	}

	@Test
	public void testDFSItPre() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		LinkedList<Node<Integer>> visit = new LinkedList<>();
		visit.addLast(n0);
		visit.addLast(n2);
		visit.addLast(n1);
		visit.addLast(n3);

		Iterator<Node<Integer>> it = tree.depthFirstSearchIt(VisitPattern.PRE_ORDER);
		Iterator<Node<Integer>> vIt = visit.iterator();
		while (it.hasNext()) {
			assertThat(vIt.hasNext(), is(true));
			assertThat(it.next().equals(vIt.next()), is(true));
		}
		assertThat(vIt.hasNext(), is(false));
	}

	@Test
	public void testDFSItPost() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		LinkedList<Node<Integer>> visit = new LinkedList<>();
		visit.addLast(n2);
		visit.addLast(n3);
		visit.addLast(n1);
		visit.addLast(n0);

		Iterator<Node<Integer>> it = tree.depthFirstSearchIt(VisitPattern.POST_ORDER);
		Iterator<Node<Integer>> vIt = visit.iterator();
		while (it.hasNext()) {
			assertThat(vIt.hasNext(), is(true));
			assertThat(it.next().equals(vIt.next()), is(true));
		}
		assertThat(vIt.hasNext(), is(false));
	}

	@Test
	public void testBFSIt() {
		NodeFSNB<Integer> n0 = tree.addRoot(0);
		NodeFSNB<Integer> n1 = tree.addChild(n0, 1);
		NodeFSNB<Integer> n2 = tree.addChild(n0, 2);
		NodeFSNB<Integer> n3 = tree.addChild(n1, 3);

		LinkedList<Node<Integer>> visit = new LinkedList<>();
		visit.addLast(n0);
		visit.addLast(n2);
		visit.addLast(n1);
		visit.addLast(n3);

		Iterator<Node<Integer>> it = tree.breadthFirstSearchIt();
		Iterator<Node<Integer>> vIt = visit.iterator();
		while (it.hasNext()) {
			assertThat(vIt.hasNext(), is(true));
			assertThat(it.next().equals(vIt.next()), is(true));
		}
		assertThat(vIt.hasNext(), is(false));
	}

}

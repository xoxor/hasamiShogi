package hs.test;

import hs.representation.Board;
import hs.util.Node;
import hs.util.NodeFSNB;
import hs.util.TreeFSNB;

import java.util.ArrayList;
import java.util.Iterator;

public class TestBoard extends Board {

	public static class Move {
		int id;
		int value;

		public Move(int id, int value) {
			this.id = id;
			this.value = value;
		}

		public String toString() {
			return "(" + id + "," + value + ")";
		}
	}

	private TreeFSNB<Move> game;
	private NodeFSNB<Move> current;

	public TestBoard(TreeFSNB<Move> game) {
		if (game == null) {
			throw new NullPointerException();
		}
		this.game = game;
		current = game.getRoot();
	}

	@Override
	public TestBoard factory() {
		// TODO this is more like a copy
		return new TestBoard(game);
	}

	@Override
	public int get(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void set(int i, int j, int pawn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInitialConfiguration() {
		super.setInitialConfiguration();
		current = game.getRoot();
	}

	@Override
	public ArrayList<int[]> getLegalMoves() {
		ArrayList<int[]> moves = new ArrayList<>();
		Iterator<Node<Move>> it = game.childrenIt(current);
		while (it.hasNext()) {
			int[] m = new int[4];
			m[0] = it.next().getInfo().id;
			moves.add(m);
		}
		return moves;
	}

	@Override
	public Iterator<int[]> legalMovesIterator() {
		ArrayList<int[]> moves = getLegalMoves();
		return moves.iterator();
	}

	@Override
	public void move(int fromR, int fromC, int toR, int toC) {
		Iterator<Node<Move>> it = game.childrenIt(current);
		while (it.hasNext()) {
			Node<Move> n = it.next();
			if (n.getInfo().id == fromR) {
				current = (NodeFSNB<Move>) n;
			}
		}
	}

	@Override
	public void unmove() {
		current = current.getParent();
	}

	@Override
	public boolean isGameFinished() {
		return current.getFirst() == null;
	}

	@Override
	public int getUtility(int player) {
		return current.getInfo().value;
	}

	public TreeFSNB<Move> getTree() {
		return game;
	}

}

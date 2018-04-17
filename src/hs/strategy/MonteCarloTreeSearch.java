package hs.strategy;

import hs.Timer;
import hs.heuristic.HeuristicFunction;
import hs.representation.Board;
import hs.util.Node;
import hs.util.NodeFSNB;
import hs.util.TreeFSNB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class MonteCarloTreeSearch implements SearchStrategy {

	private int plyThreshold;
	private long timeThreshold;
	private HeuristicFunction h;
	private int player;

	private double Cp;

	private static class Info {
		int[] action;
		int q;
		int n;
		ArrayList<int[]> moves;

		int d;

		public Info(Board b) {
			if (!b.isGameFinished()) {
				moves = b.getLegalMoves();
			} else {
				moves = new ArrayList<>();
			}
		}

		public String toString() {
			return "action=" + Arrays.toString(action) + "\n" + "q=" + q
					+ "\nn=" + n + "\nmoves=" + (moves.size() > 0);
		}
	}

	private TreeFSNB<Info> tree;
	private Random r;

	private Timer timer;

	public MonteCarloTreeSearch(int movesThreshold, long timeThreshold,
			HeuristicFunction h, double Cp, int player) {
		if (movesThreshold <= 0) {
			throw new IllegalArgumentException("Moves must be positive! "
					+ movesThreshold);
		}
		if (timeThreshold <= 0) {
			throw new IllegalArgumentException("Time must be positive! "
					+ timeThreshold);
		}
		if (h == null) {
			throw new NullPointerException();
		}
		this.plyThreshold = movesThreshold;
		this.timeThreshold = timeThreshold;
		this.h = h;
		this.Cp = Cp;
		this.player = player;

		tree = new TreeFSNB<>();
		r = new Random();

		timer = new Timer(timeThreshold);
	}

	@Override
	public int[] nextMove(Board b) {
		timer.start();

		tree.clear();
		Info i = new Info(b);
		i.d = 0;
		tree.addRoot(i);
		while (!timer.isExpired()) {
			NodeFSNB<Info> vl = treePolicy(b, tree.getRoot());
			int delta = defaultPolicy(b);
			backup(b, vl, delta);
		}
		return bestChild(b, tree.getRoot(), 0).getInfo().action;
	}

	private NodeFSNB<Info> treePolicy(Board b, NodeFSNB<Info> v) {
		while (v.getFirst() != null || !b.isGameFinished()) {
			ArrayList<int[]> moves = v.getInfo().moves;
			if (moves.size() > 0) {
				int a = r.nextInt(moves.size());
				b.move(moves.get(a));
				Info info = new Info(b);
				info.action = moves.get(a);
				info.d = v.getInfo().d + 1;
				moves.remove(a);
				return tree.addChild(v, info);
			} else {
				v = bestChild(b, v, Cp);
				b.move(v.getInfo().action);
			}
		}
		return v;
	}

	private NodeFSNB<Info> bestChild(Board b, NodeFSNB<Info> v, double c) {
		ArrayList<NodeFSNB<Info>> nMax = new ArrayList<>();
		double max = Double.NEGATIVE_INFINITY;
		Iterator<Node<Info>> it = tree.childrenIt(v);
		while (it.hasNext()) {
			Node<Info> child = it.next();
			Info i = child.getInfo();
			double temp = ((double) i.q) / i.n + c
					* Math.sqrt((2 * Math.log(v.getInfo().n)) / (i.n))
					+ h.evaluate(b, i.d) / i.n;
			if (temp >= max) {
				if (temp > max) {
					nMax.clear();
					max = temp;
				}
				nMax.add((NodeFSNB<Info>) child);
			}
		}
		return nMax.get(r.nextInt(nMax.size()));
	}

	private int defaultPolicy(Board board) {
		Board b = board.copy();
		while (!b.isGameFinished()) {
			ArrayList<int[]> moves = b.getLegalMoves();
			int a = r.nextInt(moves.size());
			b.move(moves.get(a));
		}
		return b.getUtility(player);
	}

	private void backup(Board b, NodeFSNB<Info> v, int delta) {
		while (v != null) {
			v.getInfo().n++;
			v.getInfo().q += delta;
			delta *= -1;
			v = v.getParent();
			if (v != null) {
				b.unmove();
			}
		}
	}

	@Override
	public int[] getLastMove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getLastValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeThreshold() {
		// TODO Auto-generated method stub
		return timeThreshold;
	}

}

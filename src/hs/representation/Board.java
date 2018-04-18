package hs.representation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public abstract class Board {

	public static final int BLACK = 0, WHITE = 1, EMPTY = -1;
	public static final int BLACK_VICTORY = 1, WHITE_VICTORY = -1, DRAW = 0,
			NOT_FINISHED = -2;
	public static final int INITIAL_PIECES_NUMBER = 18;
	public static final int BOARD_SIZE = 9;

	private int turn;
	private boolean terminated;
	private int esit;
	private int[] pieces;
	private int remainingMoves;
	private int maxTurns;

	protected static class MoveInfo {
		// 4+2 vertical + 6 horizontal
		private static final int MAX_SANDWICHED = 12;

		int turn;
		int fromR;
		int fromC;
		int toR;
		int toC;
		int[] sandwiched = new int[MAX_SANDWICHED * 2];
		int i = 0;

		public MoveInfo() {
		} 

		public MoveInfo(MoveInfo m) {
			this.turn = m.turn;
			this.fromR = m.fromR;
			this.fromC = m.fromC;
			this.toR = m.toR;
			this.toC = m.toC;
			this.sandwiched = new int[MAX_SANDWICHED * 2];
			for (int i = 0; i < this.sandwiched.length; i++) {
				this.sandwiched[i] = m.sandwiched[i];
			}
			this.i = 0;
		}
	}

	private LinkedList<MoveInfo> history;

	public Board() {
		pieces = new int[2];
		history = new LinkedList<>();
		remainingMoves = Integer.MAX_VALUE;
		maxTurns = Integer.MAX_VALUE;
	}

	public Board(int maxTurns) {
		pieces = new int[2];
		history = new LinkedList<>();
		this.remainingMoves = maxTurns;
		this.maxTurns = maxTurns;
	}

	public Board copy() {
		Board copy = factory();
		copy.esit = esit;
		copy.turn = turn;
		copy.terminated = terminated;
		copy.remainingMoves = remainingMoves;
		copy.maxTurns = maxTurns;
		copy.pieces = new int[2];
		copy.pieces[BLACK] = pieces[BLACK];
		copy.pieces[WHITE] = pieces[WHITE];
		copy.history = new LinkedList<>();
		for (MoveInfo m : history) {
			copy.history.add(new MoveInfo(m));
		}

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				copy.set(i, j, get(i, j));
			}
		}
		return copy;
	}

	public abstract Board factory();

	public abstract int get(int i, int j);

	protected abstract void set(int i, int j, int pawn);

	public void setCustomizedConfiguration(int[][] config, int turn) {
		// config assumed correct
		pieces[BLACK] = 0;
		pieces[WHITE] = 0;
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				int box = config[i][j];
				set(i, j, box);
				if (box == BLACK) {
					pieces[BLACK]++;
				}
				if (box == WHITE) {
					pieces[WHITE]++;
				}
			}
		}
		if (pieces[WHITE] <= 1 || pieces[WHITE] > 18 || pieces[BLACK] <= 1
				|| pieces[BLACK] > 18) {
			throw new IllegalArgumentException(
					"Wrong pawns configuration! #BLACK= " + pieces[BLACK]
							+ " #WHITE= " + pieces[WHITE]);
		}
		this.turn = turn;
		terminated = false;
		esit = NOT_FINISHED;
		history.clear();
	}

	public void setInitialConfiguration() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (i <= 1) {
					set(i, j, BLACK);
				} else if (i <= 6) {
					set(i, j, EMPTY);
				} else {
					set(i, j, WHITE);
				}
			}
		}
		remainingMoves = this.maxTurns;
		turn = BLACK;
		terminated = false;
		esit = NOT_FINISHED;
		pieces[BLACK] = INITIAL_PIECES_NUMBER;
		pieces[WHITE] = INITIAL_PIECES_NUMBER;
		history.clear();
	}

	public int currentTurn() {
		return turn;
	}

	public void move(int fromR, int fromC, int toR, int toC) {
		if(!isMoveLegal(fromR, fromC, toR, toC)) {
			throw new IllegalMoveException();
		}	
		MoveInfo info = new MoveInfo();
		info.turn = turn;
		info.fromR = fromR;
		info.fromC = fromC;
		info.toR = toR;
		info.toC = toC;

		int piece = get(fromR, fromC);
		set(fromR, fromC, EMPTY);
		set(toR, toC, piece);
		remainingMoves--;
		checkSandwich(toR, toC, info);
		terminated = checkTerminal(toR, toC);
		turn = 1 - turn;

		history.addFirst(info);
	}

	public void move(int[] move) {
		move(move[0], move[1], move[2], move[3]);
	}

	public void unmove() {
		MoveInfo info = history.removeFirst();
		// change turn
		int opponent = turn;
		turn = info.turn;
		terminated = false;
		esit = NOT_FINISHED;
		remainingMoves++;
		// unmove
		set(info.toR, info.toC, EMPTY);
		set(info.fromR, info.fromC, turn);
		// recover sandwiched
		while (info.i > 0) {
			info.i--;
			int i = info.sandwiched[info.i * 2];
			int j = info.sandwiched[info.i * 2 + 1];
			set(i, j, opponent);
			pieces[opponent]++;
		}
	}

	public boolean isMoveLegal(int fromR, int fromC, int toR, int toC) {
		if (isGameFinished()) {
			return false;
		}
		if ((fromR < 0 || fromR > 8) || (fromC < 0 || fromC > 8)
				|| (toR < 0 || toR > 8) || (toC < 0 || toC > 8)) {
			return false;
		}
		if (get(fromR, fromC) != turn) {
			return false;
		}
		if (get(fromR, fromC) == EMPTY) {
			return false;
		}
		if (get(toR, toC) != EMPTY) {
			return false;
		}
		// out of the cross
		if ((fromC != toC && fromR != toR) || (fromR == toR && fromC == toC)) {
			return false;
		}
		int minR = (fromR <= toR) ? fromR : toR;
		int maxR = (fromR > toR) ? fromR : toR;
		int minC = (fromC <= toC) ? fromC : toC;
		int maxC = (fromC > toC) ? fromC : toC;
		// if jump return true
		if ((fromR == toR) && (maxC - minC == 2)) {
			return true;
		}
		if ((fromC == toC) && (maxR - minR == 2)) {
			return true;
		}
		// not bounded by opponent
		for (int i = minR + 1; i < maxR; i++) {
			if (get(i, fromC) != EMPTY) {
				return false;
			}
		}
		for (int j = minC + 1; j < maxC; j++) {
			if (get(fromR, j) != EMPTY) {
				return false;
			}
		}
		return true;
	}

	public boolean isMoveLegal(int[] move) {
		int fromR = move[0];
		int fromC = move[1];
		int toR = move[2];
		int toC = move[3];
		return isMoveLegal(fromR, fromC, toR, toC);
	}

	private void checkSandwich(int r, int c, MoveInfo info) {
		int i, j;
		int myColor = get(r, c);
		int opponentColor = 1 - myColor;

		// NORTH
		i = r - 1;
		j = c;
		while (i >= 0 && (get(i, j) == opponentColor)) {
			i--;
		}
		if (i >= 0 && (get(i, j) == myColor)) {
			i++;
			while (i < r) {
				// info.sandwiched[info.i] = new int[2];
				info.sandwiched[info.i * 2] = i;
				info.sandwiched[info.i * 2 + 1] = j;
				info.i++;

				set(i, j, EMPTY);
				pieces[opponentColor]--;
				i++;
			}
		}

		// WEST
		i = r;
		j = c - 1;
		while (j >= 0 && (get(i, j) == opponentColor)) {
			j--;
		}
		if (j >= 0 && (get(i, j) == myColor)) {
			j++;
			while (j < c) {
				// info.sandwiched[info.i] = new int[2];
				info.sandwiched[info.i * 2] = i;
				info.sandwiched[info.i * 2 + 1] = j;
				info.i++;

				set(i, j, EMPTY);
				pieces[opponentColor]--;
				j++;
			}
		}

		// SOUTH
		i = r + 1;
		j = c;
		while (i <= 8 && (get(i, j) == opponentColor)) {
			i++;
		}
		if (i <= 8 && (get(i, j) == myColor)) {
			i--;
			while (i > r) {
				// info.sandwiched[info.i] = new int[2];
				info.sandwiched[info.i * 2] = i;
				info.sandwiched[info.i * 2 + 1] = j;
				info.i++;

				set(i, j, EMPTY);
				pieces[opponentColor]--;
				i--;
			}
		}

		// EAST
		i = r;
		j = c + 1;
		while (j <= 8 && (get(i, j) == opponentColor)) {
			j++;
		}
		if (j <= 8 && (get(i, j) == myColor)) {
			j--;
			while (j > c) {
				// info.sandwiched[info.i] = new int[2];
				info.sandwiched[info.i * 2] = i;
				info.sandwiched[info.i * 2 + 1] = j;
				info.i++;

				set(i, j, EMPTY);
				pieces[opponentColor]--;
				j--;
			}
		}
	}

	private boolean checkTerminal(int r, int c) {
		final int N = 5;
		int myColor = get(r, c);
		esit = (myColor == BLACK) ? BLACK_VICTORY : WHITE_VICTORY;

		int northOffset = (myColor == BLACK) ? 2 : 0;
		int southOffset = (myColor == BLACK) ? 0 : 2;

		if ((pieces[BLACK] == 1 && pieces[WHITE] == 1)
				|| (pieces[BLACK] == 0 && pieces[WHITE] == 0)) {
			esit = DRAW;
			return true;
		}

		if (this.remainingMoves == 0) {
			if (pieces[BLACK] == pieces[WHITE]) {
				esit = DRAW;
			} else {
				esit = pieces[BLACK] > pieces[WHITE] ? BLACK_VICTORY
						: WHITE_VICTORY;
			}
			return true;
		}
		int opponent = 1 - myColor;
		if (pieces[opponent] <= 1) {
			return true;
		}

		// VERTICAL
		int count = 0;
		for (int i = r; (i >= 0 + northOffset) && (i <= 8 - southOffset); i--) {
			if (get(i, c) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}
		for (int i = r + 1; (i >= 0 + northOffset) && (i <= 8 - southOffset); i++) {
			if (get(i, c) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}

		// DIAGONAL
		count = 0;
		for (int i = r, j = c; (i >= 0 + northOffset) && (i <= 8 - southOffset)
				&& (j >= 0); i--, j--) {
			if (get(i, j) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}
		for (int i = r + 1, j = c + 1; (i >= 0 + northOffset)
				&& (i <= 8 - southOffset) && (j <= 8); i++, j++) {
			if (get(i, j) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}

		// INVERSE DIAGONAL
		count = 0;
		for (int i = r, j = c; (i >= 0 + northOffset) && (i <= 8 - southOffset)
				&& (j <= 8); i--, j++) {
			if (get(i, j) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}
		for (int i = r + 1, j = c - 1; (i >= 0 + northOffset)
				&& (i <= 8 - southOffset) && (j >= 0); i++, j--) {
			if (get(i, j) != myColor) {
				break;
			} else {
				count++;
			}
			if (count == N) {
				return true;
			}
		}

		esit = NOT_FINISHED;
		return false;
	}

	public boolean isGameFinished() {
		return terminated;
	}

	public int getUtility(int player) {
		if (!isGameFinished()) {
			throw new IllegalStateException();
		}
		if (player != BLACK && player != WHITE) {
			throw new IllegalArgumentException(player
					+ " is not a valid player!");
		}
		if (esit == BLACK_VICTORY || esit == WHITE_VICTORY) {
			int winner = esit == BLACK_VICTORY ? BLACK : WHITE;
			return player == winner ? 1 : -1;
		} else {
			return 0;
		}
	}

	public int getEsit() {
		return esit;
	}

	public ArrayList<int[]> getLegalMoves() {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		if (!isGameFinished()) {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (get(i, j) == turn) {
						if(turn==Board.WHITE){
							for (int k = 0; k < BOARD_SIZE; k++) {
								if (isMoveLegal(i, j, k, j)) {
									moves.add(Boards.newMove(i, j, k, j));
								}
							}
						}else{
							for (int k = BOARD_SIZE-1; k >=0 ; k--) {
								if (isMoveLegal(i, j, k, j)) {
									moves.add(Boards.newMove(i, j, k, j));
								}
							}
						}
						
						for (int k = 0; k < BOARD_SIZE; k++) {
							if (isMoveLegal(i, j, i, k)) {
								moves.add(Boards.newMove(i, j, i, k));
							}
						}
					}
				}
			}
		}
		return moves;
	}

	public Iterator<int[]> legalMovesIterator() {
		return new LegalMovesIterator();
	}

	public int getOpponent(int player) {
		if (player != BLACK && player != WHITE) {
			throw new IllegalArgumentException();
		}
		return 1 - player;
	}

	public int getPiecesNumber(int player) {
		return pieces[player];
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-------------------\n");
		for (int i = 0; i < Board.BOARD_SIZE; i++) {
			sb.append("|");
			for (int j = 0; j < Board.BOARD_SIZE; j++) {
				String s = (get(i, j) == Board.BLACK) ? "*"
						: ((get(i, j) == Board.WHITE) ? "O" : " ");
				sb.append(s + "|");
			}
			sb.append(" " + (char) ('A' + i) + "\n");
		}
		sb.append("-------------------");
		return sb.toString();
	}

	private class LegalMovesIterator implements Iterator<int[]> {

		private static final int VERTICAL = 0, HORIZONTAL = 1;

		private int[] nextMove;
		private int i, j, k;
		private int direction;
		private boolean nextFound;

		public LegalMovesIterator() {
			nextMove = new int[4];
			i = 0;
			j = 0;
			k = turn==Board.WHITE? 0:(BOARD_SIZE-1);
			direction = VERTICAL;
			update();
		}

		@Override
		public boolean hasNext() {
			return nextFound;
		}

		@Override
		public int[] next() {
			if (!nextFound) {
				throw new NoSuchElementException();
			}
			int[] currentMove = new int[4];
			currentMove[0] = nextMove[0];
			currentMove[1] = nextMove[1];
			currentMove[2] = nextMove[2];
			currentMove[3] = nextMove[3];
			update();
			return currentMove;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void update() {
			nextFound = false;
			if (isGameFinished()) {
				return;
			}
			while (i < BOARD_SIZE && !nextFound) {
				if (j == BOARD_SIZE) {
					j = 0;
				}
				while (j < BOARD_SIZE && !nextFound) {
					if (get(i, j) == turn) {
						if (direction == VERTICAL) {
							if(turn==Board.WHITE){
								while (k < BOARD_SIZE && !nextFound) {
									if (isMoveLegal(i, j, k, j)) {
										nextMove[0] = i;
										nextMove[1] = j;
										nextMove[2] = k;
										nextMove[3] = j;
										nextFound = true;
									}
									k++;
								}
								if (k == BOARD_SIZE) {
									k = 0;
									direction = HORIZONTAL;
								}
							}else{
								if(turn==Board.BLACK){
									while (k >= 0 && !nextFound) {
										if (isMoveLegal(i, j, k, j)) {
											nextMove[0] = i;
											nextMove[1] = j;
											nextMove[2] = k;
											nextMove[3] = j;
											nextFound = true;
										}
										k--;
									}
									if (k < 0) {
										k = 0;
										direction = HORIZONTAL;
									}
								}
							}
						}
						if (direction == HORIZONTAL) {
							while (k < BOARD_SIZE && !nextFound) {
								if (isMoveLegal(i, j, i, k)) {
									nextMove[0] = i;
									nextMove[1] = j;
									nextMove[2] = i;
									nextMove[3] = k;
									nextFound = true;
								}
								k++;
							}
							if (k == BOARD_SIZE) {
								k = turn==Board.WHITE? 0:(BOARD_SIZE-1);
								direction = VERTICAL;
								if (nextFound) {
									j++;
								}
							}
						}
					}
					if (!nextFound) {
						j++;
					}
				}
				if (!nextFound) {
					i++;
				}
			}
		}

	}

}

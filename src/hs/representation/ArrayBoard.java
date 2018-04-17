package hs.representation;

public class ArrayBoard extends Board {

	private int[] board;

	public ArrayBoard() {
		super();
		board = new int[BOARD_SIZE * BOARD_SIZE];
	}
	
	public ArrayBoard(int max) {
		super(max);
		board = new int[BOARD_SIZE*BOARD_SIZE];
	}

	@Override
	public int get(int i, int j) {
		return board[i * BOARD_SIZE + j];
	}

	@Override
	protected void set(int i, int j, int pawn) {
		board[i * BOARD_SIZE + j] = pawn;
	}

	@Override
	public ArrayBoard factory() {
		return new ArrayBoard();
	}

}

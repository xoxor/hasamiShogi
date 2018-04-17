package hs.representation;

public class MatrixBoard extends Board {

	private int[][] board;

	public MatrixBoard() {
		super();
		board = new int[BOARD_SIZE][BOARD_SIZE];
	}
	
	public MatrixBoard(int max) {
		super(max);
		board = new int[BOARD_SIZE][BOARD_SIZE];
	}

	@Override
	public int get(int i, int j) {
		return board[i][j];
	}

	@Override
	protected void set(int i, int j, int pawn) {
		board[i][j] = pawn;
	}

	@Override
	public MatrixBoard factory() {
		return new MatrixBoard();
	}

}

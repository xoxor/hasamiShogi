package hs.representation;

public class MatrixByteBoard extends Board {

	private byte[][] board;

	public MatrixByteBoard() {
		super();
		board = new byte[BOARD_SIZE][BOARD_SIZE];
	}

	public MatrixByteBoard(int max) {
		super(max);
		board = new byte[BOARD_SIZE][BOARD_SIZE];
	}

	@Override
	public int get(int i, int j) {
		return board[i][j];
	}

	@Override
	protected void set(int i, int j, int pawn) {
		board[i][j] = (byte) pawn;
	}

	@Override
	public MatrixByteBoard factory() {
		return new MatrixByteBoard();
	}

}

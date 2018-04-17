package hs.representation;

public final class Boards {

	private static char c[] = new char[4];

	private Boards() {
	}

	public static void drawBoard(Board b) {
		System.out.println("-------------------");
		for (int i = 0; i < Board.BOARD_SIZE; i++) {
			System.out.print("|");
			for (int j = 0; j < Board.BOARD_SIZE; j++) {
				String s = (b.get(i, j) == Board.BLACK) ? "*"
						: ((b.get(i, j) == Board.WHITE) ? "O" : " ");
				System.out.print(s + "|");
			}
			System.out.println(" " + (char) ('A' + i));
		}
		System.out.println("-------------------");
		for (int i = 0; i < 9; i++) {
			System.out.print(" " + (9 - i));
		}
		System.out.println();
	}

	public static void decodeMove(String move, int[] pos) {
		move = move.toUpperCase();
		pos[0] = move.charAt(0) - 65;
		pos[1] = 8 - (move.charAt(1) - '1');
		pos[2] = move.charAt(2) - 65;
		pos[3] = 8 - (move.charAt(3) - '1');
	}

	public static String encodeMove(int[] pos) {
		c[0] = Character.toChars(65 + pos[0])[0];
		c[1] = (char) ('9' - pos[1]);
		c[2] = Character.toChars(65 + pos[2])[0];
		c[3] = (char) ('9' - pos[3]);
		return new String(c);
	}

	public static int[] newMove(int fromR, int fromC, int toR, int toC) {
		int[] move = new int[4];
		move[0] = fromR;
		move[1] = fromC;
		move[2] = toR;
		move[3] = toC;
		return move;
	}

}

package hs.test;

import hs.gui.GUI;
import hs.representation.Board;
import hs.representation.Boards;
import hs.representation.IllegalMoveException;
import hs.representation.MatrixBoard;

public class TestGui {

	public static void main(String[] args) {
		Board b = new MatrixBoard();
		b.setInitialConfiguration();

		GUI g = new GUI();	
		g.setBoard(b);

		int[] pos = new int[4];

		while (!b.isGameFinished()) {
			String move = g.getMove();
			Boards.decodeMove(move, pos);
			try { 
				b.move(pos[0], pos[1], pos[2], pos[3]);				
			} catch (IllegalMoveException e){
				g.showError(move);
			}
			g.upgradedGrid();
		}
		g.showWinner();	
	}
}

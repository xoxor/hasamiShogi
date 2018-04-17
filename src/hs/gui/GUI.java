package hs.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import hs.representation.Board;

import java.awt.Font;

public class GUI {

	private Semaphore s = new Semaphore(0);
	public static final String StringBLACK = "*", StringWHITE = "O", StringEMPTY = "";
	private JFrame frame;
	private JButton[][] buttons;
	private Board b;
	private boolean firstPressed = false;
	private boolean moveRequired = false;
	private String move;
	private JLabel lblTextMossa;
	private JLabel lblTextTurn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.out.println("Unable to load Windows look and feel");
		}
		initialize();
		frame.setVisible(true);
	}

	public void setBoard(Board b) {
		this.b = b;
		upgradedGrid();
	}

	public String getMove() {
		lblTextMossa.setText("SELECT MOVE");
		moveRequired = true;
		try {
			s.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String r = new String(move);
		firstPressed = false;
		move = null;
		moveRequired = false;
		return r;
	}

	public void showError(String move) {
		JOptionPane.showMessageDialog(null, move + " not valid!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void showWinner() {
		String s;
		switch (b.getEsit()) {
		case Board.BLACK_VICTORY:
			s = "Black ("+StringBLACK+") WINS";
			break;
		case Board.WHITE_VICTORY:
			s = "White ("+StringWHITE+") WINS";
			break;
		case Board.DRAW:
			s = "DRAW!";
			break;
		case Board.NOT_FINISHED:
			s = "NOT FINISHED";
			break;
		default:
			s = "Illegal";
			break;
		}
		JOptionPane.showMessageDialog(null, s);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 650, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelButton = new JPanel();
		panel.add(panelButton, BorderLayout.CENTER);
		panelButton.setLayout(new GridLayout(11, 11, 0, 0));

		JSplitPane splitPaneTop = new JSplitPane();
		splitPaneTop.setResizeWeight(0.5);
		panel.add(splitPaneTop, BorderLayout.NORTH);

		JLabel lblTurn = new JLabel("Turn:");
		lblTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblTurn.setFont(new Font("Tahoma", Font.BOLD, 11));
		splitPaneTop.setLeftComponent(lblTurn);

		lblTextTurn = new JLabel();
		lblTextTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblTextTurn.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTextTurn.setForeground(Color.BLUE);
		splitPaneTop.setRightComponent(lblTextTurn);

		JSplitPane splitPaneDown = new JSplitPane();
		splitPaneDown.setResizeWeight(0.5);
		panel.add(splitPaneDown, BorderLayout.SOUTH);

		JLabel lblMossa = new JLabel("Last pressed:");
		lblMossa.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMossa.setHorizontalAlignment(SwingConstants.CENTER);
		splitPaneDown.setLeftComponent(lblMossa);

		lblTextMossa = new JLabel();
		lblTextMossa.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTextMossa.setHorizontalAlignment(SwingConstants.CENTER);
		splitPaneDown.setRightComponent(lblTextMossa);

		// create buttons
		buttons = new JButton[11][11];
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[0].length; j++) {
				buttons[i][j] = new JButton(StringEMPTY);
				buttons[i][j].setFont(new Font("Tahoma", Font.BOLD, 14));
				panelButton.add(buttons[i][j]);
			}
		}
		//disable those i don't need
		for (int i = 0; i < 11; i++) {
			buttons[i][0].setEnabled(false);
			buttons[i][10].setEnabled(false);
			buttons[0][i].setEnabled(false);
			buttons[10][i].setEnabled(false);
		}
		for (int i = 1; i < 10; i++) {
			buttons[0][i].setText(10 - i + "");
			buttons[10][i].setText(10 - i + "");
			buttons[i][0].setText("" + Character.toChars(64 + i)[0]);
			buttons[i][10].setText("" + Character.toChars(64 + i)[0]);

			// upgradedGrid();
		}

		// Listener
		ButtonListener bListener = new ButtonListener();
		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				buttons[i][j].addActionListener(bListener); 
			}
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (moveRequired) {
				JButton pressed = (JButton) e.getSource();
				String pos = getPosition(pressed);
				handleMove(pos);
			}
		}

		private void handleMove(String pos) {
			if (!firstPressed) {
				move = pos;
				firstPressed = true;
				lblTextMossa.setText(move);
			} else {
				move = move + pos;
				lblTextMossa.setText(move);
				s.release();
			}
		}

		private String getPosition(JButton pressed) {
			for (int i = 1; i < 10; i++) {
				for (int j = 1; j < 10; j++) {
					if (buttons[i][j] == pressed) {
						return ("" + Character.toChars(64 + i)[0]) + (10 - j);
					}
				}
			}
			return null;
		}
	}

	public void upgradedGrid() {
		int turn = b.currentTurn();
		lblTextTurn.setText(turn == Board.BLACK ? StringBLACK : turn == Board.WHITE ? StringWHITE : " ");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int pawn = b.get(i, j);
				buttons[i + 1][j + 1]
						.setText(pawn == Board.BLACK ? StringBLACK : pawn == Board.WHITE ? StringWHITE : StringEMPTY);
			}
		}
	}

}

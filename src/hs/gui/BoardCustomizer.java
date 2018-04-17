package hs.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import hs.representation.Board;

import javax.swing.JRadioButton;
import java.awt.FlowLayout;


public class BoardCustomizer {
	
	public static void setGuiConfiguration(Board b){
		GuiConf c = new GuiConf(b);
		c.ready(); 
	}
}

class GuiConf {
	private Semaphore s = new Semaphore(0);
	private Board b;
	private JFrame frame;
	private JButton[][] buttons;
	private JButton turnButton;
	private JRadioButton radioButton1, radioButton2, radioButton3;
	public static final String StringBLACK = "*", StringWHITE = "O", StringEMPTY = "";
	private int[][] matrix = new int[9][9];


	public GuiConf(Board b) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)	{
			System.out.println("Unable to load Windows look and feel");
		}
		this.b=b;
		initialize();
		frame.setVisible(true);
	}
	
	public void ready(){
		try {
			s.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.setVisible(false);
		frame.dispose();
	}
	
	private void showError(){
		JOptionPane.showMessageDialog(null, "Not valid configuration! Check pawns number", "Error", JOptionPane.ERROR_MESSAGE);
	}

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

		JLabel lblTurn = new JLabel("Start turn:");
		lblTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblTurn.setFont(new Font("Tahoma", Font.BOLD, 11));
		splitPaneTop.setLeftComponent(lblTurn);

		turnButton = new JButton(StringBLACK);
		turnButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		splitPaneTop.setRightComponent(turnButton);

		JSplitPane splitPaneDown = new JSplitPane();
		splitPaneDown.setResizeWeight(0.5);
		panel.add(splitPaneDown, BorderLayout.SOUTH);

		JButton doneButton = new JButton("DONE");
		doneButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		splitPaneDown.setRightComponent(doneButton);

		JPanel radioPanel = new JPanel();
		splitPaneDown.setLeftComponent(radioPanel);
		radioPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		radioButton1 = new JRadioButton(StringBLACK);
		radioButton1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		radioButton1.setSelected(true);
		radioPanel.add(radioButton1);

		radioButton2 = new JRadioButton(StringWHITE);
		radioButton2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		radioPanel.add(radioButton2);

		radioButton3 = new JRadioButton("Empty");
		radioButton3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		radioPanel.add(radioButton3);

		ButtonGroup group = new ButtonGroup();
		group.add(radioButton1);
		group.add(radioButton2);
		group.add(radioButton3);

		//create buttons
		buttons = new JButton[11][11];
		for(int i = 0; i<buttons.length; i++){
			for(int j = 0; j<buttons[0].length; j++){
				buttons[i][j] = new JButton(StringEMPTY);
				buttons[i][j].setFont(new Font("Tahoma", Font.BOLD, 14));
				panelButton.add(buttons[i][j]); 
			}
		}
		//disable those I don't need
		for(int i = 0; i<11; i++){  
			buttons[i][0].setEnabled(false); 
			buttons[i][10].setEnabled(false);
			buttons[0][i].setEnabled(false);
			buttons[10][i].setEnabled(false);
		}
		for(int i=1; i<10; i++){
			buttons[0][i].setText(10-i+"");
			buttons[10][i].setText(10-i+"");
			buttons[i][0].setText(""+Character.toChars(64+i)[0]);
			buttons[i][10].setText(""+Character.toChars(64+i)[0]);
		}

		// Listener 
		ButtonListener bListener = new ButtonListener();
		for(int i = 1; i<10; i++){
			for(int j = 1; j<10; j++){
				buttons[i][j].addActionListener(bListener); //aggiungo un listener a tutti i bottoni
			}
		}

		TurnListener tListener = new TurnListener();
		turnButton.addActionListener(tListener);

		DoneListener dListener = new DoneListener();
		doneButton.addActionListener(dListener);

	}

	private class ButtonListener implements ActionListener {  
		public void actionPerformed(ActionEvent e) {
			JButton pressed = (JButton) e.getSource();
			String t = pressed.getText();
			if (radioButton1.isSelected()){
				pressed.setText(t.equals(StringBLACK) ? StringEMPTY : StringBLACK);
			} else if (radioButton2.isSelected()){
				pressed.setText(t.equals(StringWHITE) ? StringEMPTY : StringWHITE);
			} else {
				pressed.setText(StringEMPTY);
			}
		}
	}

	private static int convert(String pawn){
		return pawn.equals(StringBLACK) ? Board.BLACK : pawn.equals(StringWHITE) ? Board.WHITE : Board.EMPTY;
	}
	
	private class TurnListener implements ActionListener {  
		public void actionPerformed(ActionEvent e) {
			JButton pressed = (JButton) e.getSource();
			pressed.setText(pressed.getText().equals(StringBLACK) ? StringWHITE : StringBLACK);
		}
	}
	
	private class DoneListener implements ActionListener {  
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					String pawn = buttons[i+1][j+1].getText();
					matrix[i][j] = convert(pawn);
				}
			}
			try {
				b.setCustomizedConfiguration(matrix, convert(turnButton.getText()));
				s.release();
			} catch (Exception ex) {
				showError();
			}
		}
	}
}

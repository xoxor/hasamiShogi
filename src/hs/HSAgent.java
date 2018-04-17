package hs;

import hs.heuristic.HeuristicFunction;
import hs.heuristic.NewHeuristic;
import hs.representation.Board;
import hs.representation.Boards;
import hs.representation.MatrixBoard;
import hs.strategy.IDAlphaBetaSearchH;
import hs.strategy.IDAlphaBetaSearchH_BestMove;
import hs.strategy.SearchStrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HSAgent {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private int thres_move;
	private int thres_time;
	private int algorithm;

	private Board board;
	private SearchStrategy st;
	private HeuristicFunction hf;

	public HSAgent(String serverAddress, int port, int thres_move,
			int thres_time, int algorithm) throws Exception {
		// Setup networking
		socket = new Socket(serverAddress, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		this.thres_move = thres_move;
		this.thres_time = thres_time;
		this.algorithm = algorithm;
		
		board = new MatrixBoard(thres_move*2);
		board.setInitialConfiguration();
		
		Thread.currentThread().setPriority(8);
	}

	String sMove;
	int[] m = new int[4];

	public void play() throws Exception {
		String response;
		Scanner sc = new Scanner(System.in);
		try {
			response = in.readLine();
			if (response.startsWith("WELCOME")) {
				String colour = response.substring(8);
				System.out.println(colour);

				int player = -1;
				if (colour.equalsIgnoreCase("BLACK")) {
					player = Board.BLACK;
				} else if (colour.equalsIgnoreCase("WHITE")) {
					player = Board.WHITE;
				} else {
					System.out.println("ERRORE COLORE: " + colour);
				}

				hf = new NewHeuristic(player);
				switch (algorithm) {
				case 0:
					st = new IDAlphaBetaSearchH(Integer.MAX_VALUE, thres_time, hf);
					break;
				case 1:
					st = new IDAlphaBetaSearchH_BestMove(Integer.MAX_VALUE, thres_time, hf);
					break;
				default:
					System.out.println("ALGORITMO SCONOSCIUTO");
					st = new IDAlphaBetaSearchH_BestMove(Integer.MAX_VALUE, thres_time, hf);
				}
			}
			// Timer t = new Timer();

			while (true) {
				response = in.readLine();
				if (response.startsWith("VALID_MOVE")) {
					System.out.println("Valid move, please wait");
				} else if (response.startsWith("OPPONENT_MOVE")) {
					sMove = response.substring(14);
					Boards.decodeMove(sMove, m);
					board.move(m);

					System.out.print("[" + System.currentTimeMillis() + "] ");
					System.out.println("Opponent move: " + sMove);
				} else if (response.startsWith("VICTORY")) {
					System.out.println("You win");
					break;
				} else if (response.startsWith("DEFEAT")) {
					System.out.println("You lose");
					break;
				} else if (response.startsWith("TIE")) {
					System.out.println("You tied");
					break;
				} else if (response.startsWith("YOUR_TURN")) {
					System.out.print("[" + System.currentTimeMillis() + "] ");

					int[] next = st.nextMove(board);
					sMove = Boards.encodeMove(next);
					out.println("MOVE " + sMove);
					board.move(next);

					Boards.drawBoard(board);

					System.out.println("Your move: " + sMove);
				} else if (response.startsWith("TIMEOUT")) {
					System.out.println("Time out");
				} else if (response.startsWith("MESSAGE")) {
					System.out.print("[" + System.currentTimeMillis() + "] ");
					System.out.println(response.substring(8));
				}
			}
		} finally {
			sc.close();
			socket.close();
		}
	}

	/**
	 * Runs the client as an application.
	 */
	public static void main(String[] args) throws Exception {
		try {
			Parameter pars = new Parameter(args);

			int thres_move = pars.thres_move; // maximum number of moves per
												// player
			int thres_time = pars.thres_time; // in milliseconds
			int waiting_time = pars.waiting_time; // in milliseconds
			String address = pars.address;
			int port = pars.port;

			int algorithm = pars.algorithm;

			int playerBlackId = pars.blackId;
			int playerWhiteId = pars.whiteId;

			HSAgent client = new HSAgent(address, port, thres_move, thres_time,
					algorithm);
			client.play();
		} catch (Exception e) {
			if (e instanceof ParsingException) {
				System.out.println("PARSING ERROR");
				System.out.println("Usage:");
				System.out.println("\t-tm <thres_move> (default Integer.MAX_VALUE moves)");
				System.out.println("\t-tt <thres_time> (default 1000 ms)");
				System.out.println("\t-wt <waiting_time> (default 30000 ms)");
				System.out.println("\t-addr <address> (default 192.168.122.1)");
				System.out.println("\t-p <port> (default 8901)");
				System.out.println("\t-alg <algorithm> (default 1)");
				System.out.println("\t-black <group_id> (default 0)");
				System.out.println("\t-white <group_id> (default 1)");
			} else
				e.printStackTrace();
		}
	}
}

class Parameter {

	public int thres_move = Integer.MAX_VALUE;
	public int thres_time = 1000;
	public String address = "192.168.122.1";
	public int port = 8901;
	public int algorithm = 1;
	public int blackId = 0;
	public int whiteId = 1;
	public int waiting_time = 10000;

	public Parameter(String[] args) throws ParsingException {
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].charAt(0) != '-')
				throw new ParsingException();
			try {
				if (args[i].equals("-tm"))
					thres_move = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-tt"))
					thres_time = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-wt"))
					waiting_time = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-addr"))
					address = args[i + 1];
				else if (args[i].equals("-p"))
					port = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-alg"))
					algorithm = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-black"))
					blackId = Integer.valueOf(args[i + 1]);
				else if (args[i].equals("-white"))
					whiteId = Integer.valueOf(args[i + 1]);
				else
					throw new ParsingException();
			} catch (Exception e) {
				throw new ParsingException();
			}

		}
	}
}

class ParsingException extends Exception {
	private static final long serialVersionUID = 7646739985905674599L;
}

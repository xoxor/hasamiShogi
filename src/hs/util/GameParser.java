package hs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class GameParser {

	private static final String MIN = "MIN";
	private static final String MAX = "MAX";

	private File f;
	private TreeFSNB<Double> game;

	public GameParser(String filename) throws FileNotFoundException {
		f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
	}

	public void parse() throws IOException {
		game = new TreeFSNB<>();
		NodeFSNB<Double> parent = game.addRoot(Double.NEGATIVE_INFINITY);
		NodeFSNB<Double> next = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			for (;;) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				StringTokenizer children = new StringTokenizer(line, ";");
				while (children.hasMoreTokens()) {
					String strChildren = children.nextToken();
					System.out.println("children= " + strChildren);
					StringTokenizer values = new StringTokenizer(strChildren,
							".");
					while (values.hasMoreTokens()) {
						String strVal = values.nextToken();
						System.out.println("values= " + strVal);
						double val;
						if (strVal.equalsIgnoreCase(MIN)) {
							val = Double.NEGATIVE_INFINITY;
						} else if (strVal.equalsIgnoreCase(MAX)) {
							val = Double.POSITIVE_INFINITY;
						} else {
							val = Double.parseDouble(strVal);
						}
						game.addChild(parent, val);
					}
					if (next == null) {
						next = parent.getFirst();
					} else {
						next = parent.getNext();
					}
					if (children.hasMoreTokens() && next == null) {
						throw new RuntimeException("Wrong children number!");
					}
				}
				parent = next;
				next = null;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	public TreeFSNB<Double> getGame() {
		if (game == null) {
			throw new IllegalStateException();
		}
		return game; // TODO copy
	}

	public String treeToString() {
		if (game == null) {
			throw new IllegalStateException();
		}
		StringBuilder sb = new StringBuilder(200);
		// TODO
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		GameParser gp = new GameParser("game_test");
		gp.parse();
		System.out.println(gp.getGame());
		System.out.println(gp.getGame().getRoot().getFirst());
		System.out.println(gp.getGame().getRoot().getFirst().getFirst());
	}

}

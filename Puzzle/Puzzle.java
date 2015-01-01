import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Puzzle {

	private enum Direction {
		HORIZONTAL, VERTICAL, DIAGONAL
	}

	private static char[][] puzzle;

	public Puzzle(String[] grid) {
		buildPuzzle(grid);
	}

	private void buildPuzzle(String[] grid) {
		int r = grid.length;
		int c = grid[0].length();

		puzzle = new char[r][c];

		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				puzzle[i][j] = grid[i].charAt(j);
			}
		}
	}

	private class WordFind implements Runnable {

		private String[] wordlist;
		private Direction direction;

		public WordFind(String[] wordlist, Direction direction) {
			this.wordlist = wordlist;
			this.direction = direction;
		}

		@Override
		public void run() {
			System.out.println("Thread launched with direction "
					+ this.direction);
			// System.out.println(Arrays.toString(findWords()));
			System.out.println(this.direction + " : " + Arrays.asList(findWords()));
		}

		private String[] findWords() {

			ArrayList<String> result = new ArrayList<String>();

			if (direction == Direction.HORIZONTAL) {
				for (int i = 0; i < wordlist.length; i++) {
					String word = wordlist[i];
					for (int j = 0; j < puzzle[0].length; j++) {
						if (buildPath(j, 0, direction).contains(word))
							result.add(word);
					}
				}
			}

			else if (direction == Direction.VERTICAL) {
				for (int i = 0; i < wordlist.length; i++) {
					String word = wordlist[i];
					for (int j = 0; j < puzzle.length; j++) {
						if (buildPath(0, j, direction).contains(word))
							result.add(word);
					}
				}
			}

			else if (direction == Direction.DIAGONAL) {
				for (int i = 0; i < wordlist.length; i++) {
					String word = wordlist[i];

					for (int j = 0; j < puzzle.length; j++) {
						if (buildPath(j, 0, direction).contains(word))
							result.add(word);
					}

					for (int k = 1; k < puzzle[0].length; k++) {
						if (buildPath(0, k, direction).contains(word))
							result.add(word);
					}

				}
			}

			else
				return null;

			return result.toArray(new String[result.size()]);
		}

		private String buildPath(int startRow, int startCol, Direction d) {

			StringBuffer buf = new StringBuffer();

			if (d == Direction.HORIZONTAL) {
				for (int r = startRow;;) {
					for (int c = 0; c < puzzle[0].length; c++)
						buf.append(puzzle[r][c]);
					break;
				}
			}

			else if (d == Direction.VERTICAL) {
				for (int r = startRow; r < puzzle.length; r++) {
					for (int c = startCol;;) {
						buf.append(puzzle[r][c]);
						break;
					}

				}
			}

			else if (d == Direction.DIAGONAL) {
				int r = startRow, c = startCol;
				while (r < puzzle.length && c < puzzle[0].length) {
					buf.append(puzzle[r++][c++]);
				}
			}

			return buf.toString();
		}

	}

	public static void main(String[] args) {

		String[] testGrid = new String[] { "NEEL", "EEYZ", "EXEZ", "LXYL" };
		Puzzle p = new Puzzle(testGrid);

		String[] testWordList = new String[] { "NEEL" };

		ExecutorService e = Executors.newFixedThreadPool(3);

		Thread t1 = new Thread(p.new WordFind(testWordList,
				Direction.HORIZONTAL));
		Thread t2 = new Thread(p.new WordFind(testWordList, Direction.VERTICAL));
		Thread t3 = new Thread(p.new WordFind(testWordList, Direction.DIAGONAL));

		e.submit(t1);
		e.submit(t2);
		e.submit(t3);

		e.shutdown();
	}

}

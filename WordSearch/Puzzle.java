/*
* Multi-threaded search example on the classic word search game 
*/

package neelcm.puzzle;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Puzzle implements SolverObserver {

	private enum Direction {
		HORIZONTAL, VERTICAL, DIAGONAL
	}

	private static char[][] puzzle;
	private static Queue<String[]> solutions;

	public Puzzle(String[] grid) {
		buildPuzzle(grid);
		solutions = new ConcurrentLinkedQueue<String[]>();
	}
	
	@Override
	public void update(Solver s) {
		addSolutionToQueue((String[])s.getUpdate());
	}
	
	@Override
	public void observeSolver(Solver s) {
		s.registerObserver(this);
	}

	private synchronized void addSolutionToQueue(String[] newSolution) {
		solutions.add(newSolution);
		System.out.println("Solutions queue updated: " + solutions);
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

	private class WordFind implements Runnable, Solver {

		private String[] wordlist;
		private Direction direction;
		
		private ArrayList<SolverObserver> observers;
		private final Object lock;
		private boolean hasUpdated;
		private String[] solution;

		public WordFind(String[] wordlist, Direction direction) {
			this.wordlist = wordlist;
			this.direction = direction;
			lock = new Object();
			observers = new ArrayList<SolverObserver>();
		}

		@Override
		public void run() {
			System.out.println("Thread launched with direction "
					+ this.direction);
			solution = findWords();
			hasUpdated = true;
			notifyObservers();
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

		@Override
		public void registerObserver(SolverObserver obj) {
			if(obj == null)
				throw new NullPointerException("Null observer");
			
			synchronized (lock) {
								
				if(!observers.contains(obj))
					observers.add(obj);
			}
		}

		@Override
		public void deregisterObserver(SolverObserver obj) {
			if(obj == null)
				throw new NullPointerException("Null observer");
			
			synchronized (lock) {
				observers.remove(obj);
			}

		}

		@Override
		public void notifyObservers() {
			ArrayList<SolverObserver> _observers = null;
			
			synchronized(lock) {
				if(!hasUpdated)
					return;
				_observers = new ArrayList<SolverObserver>(this.observers);
				this.hasUpdated = false;
			}
						
			for(SolverObserver obj : _observers)
				obj.update(this);

		}

		@Override
		public Object getUpdate() {
			return this.solution;
		}

	}

	public static void main(String[] args) {

		String[] testGrid = new String[] { "NEEL", "EEYZ", "EXEZ", "LXYL" };
		String[] testWordList = new String[] { "NEEL" };

		Puzzle p = new Puzzle(testGrid);
		ExecutorService e = Executors.newFixedThreadPool(3);
		ArrayList<Solver> solvers = new ArrayList<Solver>();
		
		solvers.add(p.new WordFind(testWordList, Direction.HORIZONTAL));
		solvers.add(p.new WordFind(testWordList, Direction.VERTICAL));
		solvers.add(p.new WordFind(testWordList, Direction.DIAGONAL));

		for(Solver s: solvers) {
			p.observeSolver(s);
			e.submit(new Thread((Runnable) s));
		}
			
		e.shutdown();
	}
}



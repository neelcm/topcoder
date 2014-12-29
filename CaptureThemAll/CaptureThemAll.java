import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;


public class CaptureThemAll {

	private final int ATOI = 96;
	private final int BOARD_MIN = 0;
	private final int BOARD_MAX = 7;
	
	private Point[] moves = new Point[]{new Point(2, 1), new Point(2, -1), 
										new Point(-2, 1), new Point(-2, -1), 
										new Point(1, 2), new Point(-1, 2), 
										new Point(1, -2), new Point(-1, -2)};
	
	private HashMap<Point, Move> chessBoard;
	private Queue<Move> queue;
	
	private boolean didCaptureRook, didCaptureQueen; 
	
	private Point Knight, Rook, Queen;
	
	class Move {
		
		public Move(Point location, int numSteps) {
			this.location = location;
			this.numSteps = numSteps;
		}
		
		public Point location;
		public int numSteps;
		
		@Override
		public String toString() {
			return "\n" + location.toString() + "\n" + numSteps + "\n";
		}
	}
	
	int fastKnight(String knight, String rook, String queen) {
		
		initBoard(knight, rook, queen);
		
		// knight --> rook; rook-->queen
		int krA = performBFS(this.Knight, this.Rook, knight, rook, queen);
		int krB = performBFS(this.Rook, this.Queen, knight, rook, queen);
		
		int krTotal = krA + krB;
		
		// knight-->queen; queen-->rook
		int kqA = performBFS(this.Knight, this.Queen, knight, rook, queen);
		int kqB = performBFS(this.Queen, this.Rook, knight, rook, queen);
		
		int kqTotal = kqA + kqB;
		
		return Integer.min(krTotal, kqTotal);
	}
	
	int performBFS(Point start, Point end, String k, String r, String q) {
		
		initBoard(k, r, q);
		
		Move initialMove = new Move(start, 0);
		
		// force knight to get picked first
		chessBoard.put(start, initialMove);
		queue.add(initialMove);
		
		while(!queue.isEmpty()) {
						
			Move m = queue.remove();
			
			if(m.location.equals(end))
				return m.numSteps;
	
			ArrayList<Move> availableMoves = getValidMoves(m.location); 
			
			for(Move nextM : availableMoves) {
				nextM.numSteps = m.numSteps + 1;
				queue.add(nextM);
			}
						
		}
		
		return -1;
	}
	
	ArrayList<Move> getValidMoves(Point p) {
		
		ArrayList<Move> validMoves = new ArrayList<Move>();
		
		for(int i = 0; i < moves.length; i++) {
			
			Point _move = moves[i]; 
			
			int _row = (int)p.getX() + (int)_move.getX();
			int _col = (int)p.getY() + (int)_move.getY();
			
			if(_row >= BOARD_MIN && _row <= BOARD_MAX && _col >= BOARD_MIN && _col <= BOARD_MAX)
				validMoves.add(chessBoard.get(new Point(_row, _col)));
		}
		
		return validMoves;
		
	}
	
	Point strToPoint(String input) {
		return new Point(getRowForLetter(input.charAt(0)), Integer.parseInt(input.charAt(1) + ""));
	}
		
	void initBoard(String knight, String rook, String queen) {
		
		this.chessBoard = new HashMap<Point, Move>();
		this.queue = new ArrayDeque<Move>();
		
		this.didCaptureRook = false;
		this.didCaptureQueen = false;
		
		for(int i = 0; i <= BOARD_MAX; i++) {
			for(int j = 0; j <= BOARD_MAX; j++) {
				Point location = new Point(i, j);
				this.chessBoard.put(location, new Move(location, 0));
			}
		}
						
		this.Knight = strToPoint(knight);
		this.Rook = strToPoint(rook);
		this.Queen = strToPoint(queen);
		
	}
	
	int getRowForLetter(char letter) {
		return letter - ATOI;
	}
	
	public static void main(String[] args) {
		CaptureThemAll cta = new CaptureThemAll();
		System.out.println(cta.fastKnight("a1", "b3", "c5"));
		System.out.println(cta.fastKnight("b1", "c3", "a3"));
		System.out.println(cta.fastKnight("a1", "a2", "b2"));
		System.out.println(cta.fastKnight("a5", "b7", "e4"));
		System.out.println(cta.fastKnight("h8", "e2", "d2"));
	}
}
/*
 * Single Round Match 233 Round 1 - Division I, Level Two
 * http://www.topcoder.com/stat?c=problem_statement&pm=3935&rd=6532
 */
import java.util.*;

public class SmartWordToy {

	private enum Edge {
		DISCOVERY, CROSS
	}
	
	private enum Vertex {
		UNVISITED, VISITED
	}
	
	private final int NUM_LETTERS = 4;
	private final int CHAR_SPACE = 26;
	private final int ATOI = 96;
	private final int NUM_VERTICES = 26 * 26 * 26 * 26;
	private final int NUM_EDGES = NUM_VERTICES;
	private final float LOAD_FACTOR = 16.0f;
	
	private Queue<String> q;
	private HashMap<Integer, Edge> edges;
	private HashMap<Integer, Integer> moves;
	private Vertex[] didVisitVertex;
	
	public int minPresses(String start, String finish, String[] forbid) {
		
		ArrayList<String> _forbid = new ArrayList<String>(Arrays.asList(forbid));
		
		// init strucs for BFS
		q = new ArrayDeque<String>();		
		edges = new HashMap<Integer, Edge>(1, LOAD_FACTOR);
		moves = new HashMap<Integer, Integer>(1, LOAD_FACTOR);
		didVisitVertex = new Vertex[NUM_VERTICES * CHAR_SPACE];
		Arrays.fill(didVisitVertex, Vertex.UNVISITED);
		
		int numMoves = performBFS(start, finish, _forbid);
		
		return numMoves;
	}
	
	private int performBFS(String input, String finish, ArrayList<String> forbid) {
		
		int numMoves = 0;
		
		// init queue, map, state list
		q.add(input);
		didVisitVertex[idxForVertex(input)] = Vertex.VISITED;
		
		int test = 0;
		
		while(!q.isEmpty()) {
						
			// get next vertex, mark it as visited
			String v = q.remove();
			
			ArrayList<String> adjVertices = getAdjacentVertices(v, finish);
			
			System.out.println("\n" + adjVertices);
						
			for(int i = 0; i < adjVertices.size(); i++) {
				
				String adjV = adjVertices.get(i);
				int idxForAdjV = idxForVertex(adjV);
			
				//System.out.println(adjV + "-->" + idxForAdjV);
				
				if(forbid.contains(adjV)) {
					System.out.println(adjV + " is forbidden");
					continue;
				}
				
				// new input combination
				else if(didVisitVertex[idxForAdjV] == Vertex.UNVISITED) {
					
					int edgeIdx = idxForEdge(v, adjV);
					
					didVisitVertex[idxForAdjV] = Vertex.VISITED;
					
					q.add(adjV);
					
					if(!edges.containsKey(edgeIdx)) {
						edges.put(edgeIdx, Edge.DISCOVERY);
						moves.put(edgeIdx, 1);
					}
						
					
					else {
						edges.put(edgeIdx, Edge.CROSS);
						moves.put(edgeIdx, moves.get(edgeIdx) + 1);
					}
						
					
				}
				
				// previously seen combination, but check if edge is 
				else {
					
					int edgeIdx = idxForEdge(v, adjV);
					
					System.out.println("(" + v + ", " + adjV + ")-->" + edgeIdx);
					// previously seen vertex, but new path
					if(edges.get(edgeIdx) == Edge.DISCOVERY) {
						edges.put(edgeIdx, Edge.CROSS);
						System.out.println(adjV + " is cross");
						moves.put(edgeIdx, moves.get(edgeIdx) + 1);
						q.add(v);
					}
				}
				
				if(adjV.equals(finish)) {
					int edgeIdx = idxForEdge(v, adjV);
					numMoves = moves.get(edgeIdx);
					System.out.println("Done - " + adjV + " in " + numMoves + " moves.");
					return numMoves;
				}
				
			}
			
		}
		
		return -1;
	}
	
	private int idxForVertex(String input) {
		
		int total = 0;
		
		for(int i = 0; i < input.length(); i++)
			total += Math.pow((int)input.charAt(i) - ATOI, i + 1);
		
		return total;
		
	}
	
	/* http://en.wikipedia.org/wiki/Pairing_function#Cantor_pairing_function */
	private int idxForEdge(String a, String b) {
		
		int _a, _b;
		
		if(a.compareTo(b) < 0) {
			_a = idxForVertex(a);
			_b = idxForVertex(b);
		}
		
		else {
			_a = idxForVertex(b);
			_b = idxForVertex(a);
		}
		
		return ((_a + _b)*(_a + _b + 1))/2 + _b;
	}
	
	private boolean shouldAdvanceForwards(String input, int idx, String finish) {
		
		char a = input.charAt(idx);
		char b = finish.charAt(idx);
		
		return a < b;
	}
	
	private ArrayList<String> getAdjacentVertices(String input, String finish) {
		
		ArrayList<String> vertices = new ArrayList<String>();
		
		for(int i = 0; i < NUM_LETTERS; i++) {
			
			//if(shouldAdvanceForwards(input, i, finish))
				vertices.add(rotate(input, i, true));
			
			//else
				vertices.add(rotate(input, i, false));
		}
		
		return vertices;
	}
	
	private String rotate(String input, int idx, boolean forwards) {
		
		char[] inputArray = input.toCharArray();
		
		// handle overflow case: reverse
		if(inputArray[idx] == 'a' && !forwards)
			inputArray[idx] = 'z';
			
		// handle overflow case: forwards
		else if(inputArray[idx] == 'z' && forwards)
			inputArray[idx] = 'a';
		
		else
			inputArray[idx] += forwards ? 1 : -1;
			
		return new String(inputArray);
		
	}
	
	public static void main(String[] args) {
			
		SmartWordToy swt = new SmartWordToy();
		
		String[] forbid = new String[]{};
		
		swt.minPresses("aaaa", "zzzz", forbid);
		
	}
	
}
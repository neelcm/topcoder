/*
 * 2014 TCO Algorithm Round 1C - Division I, Level One
 * http://community.topcoder.com/stat?c=problem_statement&pm=13067
 */

import java.util.Arrays;

public class Unique {

	private final int ATOI = 97;
	private final int NUM_LETTERS = 26;
	private boolean[] charSeen;
	
	public Unique() {
		charSeen = new boolean[NUM_LETTERS];
		Arrays.fill(charSeen, false);
	}
	
	public String removeDuplicates(String S) {
		
		String result = "";
		
		for(int i = 0; i < S.length(); i++) {
			
			char c = S.charAt(i);
			int cIdx = c - ATOI;
			
			if(!charSeen[cIdx]) {
				result += c;
				charSeen[cIdx] = true;
			}
		}
		
		Arrays.fill(charSeen, false);
		
		return result;
	}
	
	public static void main(String[] args) {
		Unique u = new Unique();
		System.out.println(u.removeDuplicates("banana"));
		System.out.println(u.removeDuplicates("aardvark"));
		System.out.println(u.removeDuplicates("xxxxx"));
		System.out.println(u.removeDuplicates("topcoder"));
	}

}

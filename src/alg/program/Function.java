package alg.program;

public enum Function implements Node {
	progn2, progn3, ifPillAhead;
	
	public static int numberOfChildren(Node n) {
		if(n == Function.progn3)
			return 3;
		else
			return 2;
	}
}

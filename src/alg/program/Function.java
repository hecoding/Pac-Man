package model.program;

public enum Function implements Node {
	ifFoodAhead, progn2, progn3;
	
	public static int numberOfChildren(Node n) {
		if(n == Function.progn3)
			return 3;
		else
			return 2;
	}
}

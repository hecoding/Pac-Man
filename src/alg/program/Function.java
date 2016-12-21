package alg.program;

public enum Function implements Node {
	P, B;
	
	public static int numberOfChildren(Node n) {
		return 2;
	}
}

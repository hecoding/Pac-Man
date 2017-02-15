package treeProgram.operator;

public enum NumberOperator implements Operator {

	EQ, NE, LT, GT, LE, GE;
	
	public static NumberOperator stringtoNumOperator (String str) {
		
		NumberOperator op = null;
		
		switch (str) {
			case "==":	op = NumberOperator.EQ;				
				break;
			case "!=":	op = NumberOperator.NE;				
				break;
			case "<":	op = NumberOperator.LT;				
				break;
			case ">":	op = NumberOperator.GT;				
				break;
			case "<=":	op = NumberOperator.LE;				
				break;
			case ">=":	op = NumberOperator.GE;				
				break;
			default:	System.err.println("NumberOperator not valid");
				break;
		}
		
		return op;
	}
}

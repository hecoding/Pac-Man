package treeProgram.operator;

public enum NumberOperator implements Operator {

	EQ, NE, LT, GT, LE, GE;
	
	public String toString() {
		String op = null;
		
		switch(this) {
		case EQ: op = "==";
			break;
		case NE: op = "!=";
			break;
		case LT: op = "<";
			break;
		case GT: op = ">";
			break;
		case LE: op = "<=";
			break;
		case GE: op = ">=";
			break;
		}
		
		return op;
	}
	
}

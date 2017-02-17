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
	
	public boolean evaluateOperator(int op1, int op2) {
		if(this == NumberOperator.EQ)
			return op1 == op2;
		else if(this == NumberOperator.GE)
			return op1 >= op2;
		else if(this == NumberOperator.GT)
			return op1 > op2;
		else if(this == NumberOperator.LE)
			return op1 <= op2;
		else if(this == NumberOperator.LT)
			return op1 > op2;
		else if(this == NumberOperator.NE)
			return op1 != op2;
		
		return false; // should throw exception?
	}
	
}

package parser.operators;

public enum NumericOperator {

	EQ {
		public boolean operate(int num1, int num2) {
			return num1 == num2;
		}
	}, NE {
		public boolean operate(int num1, int num2) {
			return num1 != num2;
		}
	}, LT {
		public boolean operate(int num1, int num2) {
			return num1 < num2;
		}
	}, GT {
		public boolean operate(int num1, int num2) {
			return num1 > num2;
		}
	}, LE {
		public boolean operate(int num1, int num2) {
			return num1 <= num2;
		}
	}, GE {
		public boolean operate(int num1, int num2) {
			return num1 >= num2;
		}
	};
	
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
		
	public abstract boolean operate(int num1, int num2);

}

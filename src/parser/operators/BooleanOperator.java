package parser.operators;

public enum BooleanOperator {
	
	and, or;
	
	public boolean operate(boolean b1, boolean b2){
		boolean result = false;
		
		if(this==BooleanOperator.and)
			result = b1 && b2;
		else if(this==BooleanOperator.or)
			result = b1 || b2;
		else
			System.err.println("unknown boolean");
		
		return result;
		
	}
	
	public String toString() {
		if(this==BooleanOperator.and)
			return " && ";
		else if (this==BooleanOperator.or)
			return " || ";
		else
			return "error";
	}
}

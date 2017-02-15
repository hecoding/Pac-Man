package treeProgram.function;

import treeProgram.operator.NumberOperator;

public enum NumberFunc implements Function {
	
	distancia;
	
	int number;
	NumberOperator nOP;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public NumberOperator getnOP() {
		return nOP;
	}

	public void setnOP(NumberOperator nOP) {
		this.nOP = nOP;
	}
	
	public String toString() {
		return this.name() + " " + this.nOP.toString() + " " + this.number;
	}
	
}

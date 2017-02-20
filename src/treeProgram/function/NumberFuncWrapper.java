package treeProgram.function;

import treeProgram.operator.NumberOperator;

public class NumberFuncWrapper implements Function {
	
	private NumberFunc nf;
	private NumberOperator nOP;
	private int number;
	
	public NumberFuncWrapper() {
	}
	
	public NumberFuncWrapper(NumberFunc nf, int number, NumberOperator nOP) {
		super();
		this.nf = nf;
		this.number = number;
		this.nOP = nOP;
	}

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
	
	public NumberFunc getNf() {
		return nf;
	}

	public void setNf(NumberFunc nf) {
		this.nf = nf;
	}

	public String toString() {
		return this.nf.toString() + " " + this.nOP.toString() + " " + this.number;
	}
	
}

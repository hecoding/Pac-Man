package parser;

import java.util.ArrayList;

import pacman.game.Game;
import parser.enums.ConditionType;
import parser.function.BooleanFunc;
import parser.function.Function;
import parser.function.NumericFunc;
import parser.operators.NumericOperator;

public class Condition {

	private ConditionType condType = null;
	private ArrayList<Function> functionList;
	private int number;
	private NumericOperator numberOp = null;
		
	public Condition(BooleanFunc bf) {										//bool
		this.condType = ConditionType.bool;
		this.functionList = new ArrayList<>();
		this.functionList.add(bf);
	}
	
	public Condition(NumericFunc nf, int num, NumericOperator nOp) {			//func_num
		this.condType = ConditionType.func_num;
		this.functionList = new ArrayList<>();
		this.functionList.add(nf);
		this.number = num;
		this.numberOp = nOp;
	}
	
	public Condition(NumericFunc nf1, NumericFunc nf2, NumericOperator nOp) {	//func_func
		this.condType = ConditionType.func_func;
		this.functionList = new ArrayList<>();
		this.functionList.add(nf1);
		this.functionList.add(nf2);
		this.numberOp = nOp;
	}

	public Condition(int num, NumericFunc nf, NumericOperator nOp) {			//num_func
		this.condType = ConditionType.num_func;
		this.functionList = new ArrayList<>();
		this.functionList.add(nf);
		this.number = num;
		this.numberOp = nOp;
	}

	public boolean evaluateCondition(Game g){		
		return condType.evaluate(functionList, number, numberOp, g);		
	}
	
	public String toString(){
		String str = "";
		if(condType == ConditionType.bool)
			str = functionList.get(0).toString();
		else if(condType == ConditionType.func_func)
			str = str + functionList.get(0).toString() + " " + numberOp.toString() + " " + functionList.get(0).toString();
		else if(condType == ConditionType.func_num)
			str = str + functionList.get(0).toString() + " " + numberOp.toString() + " " + number;
		else if(condType == ConditionType.num_func)
			str = str + " " + number + numberOp.toString() + " " + functionList.get(0).toString();
		
		return str;
	}
}

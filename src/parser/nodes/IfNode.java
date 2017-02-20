package parser.nodes;

import java.util.ArrayList;

import pacman.game.Game;
import parser.Condition;
import parser.operators.BooleanOperator;

public class IfNode extends Node {
	private ArrayList<Condition> conditionList;
	private ArrayList<BooleanOperator> boolOpList;
	
	public IfNode(){
		super();
		conditionList = new ArrayList<>();
		boolOpList = new ArrayList<>();
	}
	
	public IfNode(ArrayList<Condition> cl, ArrayList<BooleanOperator> bopl){
		super();
		conditionList = cl;
		boolOpList = bopl;
	}
	
	public Node execute(Game g){
		boolean ev = this.evaluateIfNode(g);
		if(ev)
			return this.children.get(0).execute(g);
		else if (!ev && hasElse())
			return this.children.get(1).execute(g);
		else {
			return null;
		}
	}
	
	private boolean hasElse(){
		return this.children.size() > 1;
	}
	
	private boolean evaluateIfNode(Game g){
		boolean result = conditionList.get(0).evaluateCondition(g);
		for (int i = 0; i < boolOpList.size();i++) {
			BooleanOperator op = boolOpList.get(i);
			Condition cond = conditionList.get(i+1);
			
			result = op.operate(result, cond.evaluateCondition(g));
		}
		return result;
	}
	
	public String toString(){
		String str = "if( " + conditionList.get(0).toString();
		for (int i = 0; i < boolOpList.size(); i++)
			str = str + boolOpList.get(i).toString() + conditionList.get(i+1).toString();
		
		str = str + " ){ ";
		if(children.size() > 0)
			str = str + children.get(0).toString();
		str = str + " }";
		if(children.size() > 1)
			str = str + " else{ " + children.get(1).toString() + " }";
		
		return str;
	}
	
	public String pretty(String tabs){
		String str = tabs + "if( " + conditionList.get(0).toString();
		for (int i = 0; i < boolOpList.size(); i++)
			str = str + boolOpList.get(i).toString() + conditionList.get(i+1).toString();
		
		str = str + " ){ " + System.lineSeparator();
		if(children.size() > 0)
			str = str + children.get(0).pretty(tabs + "\t");
		str = str + System.lineSeparator() + tabs + " }" + System.lineSeparator();
		if(children.size() > 1)
			str = str + tabs + "else{ " + System.lineSeparator() + children.get(1).pretty(tabs + "\t") + System.lineSeparator() + tabs + " }" ;
		
		return str;
	}
}

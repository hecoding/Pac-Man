package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pacman.game.Game;
import parser.operators.BooleanOperator;
import parser.function.BooleanFunc;
import parser.function.NumericFunc;
import parser.function.Action;
import parser.nodes.IfNode;
import parser.nodes.NicerTree;
import parser.nodes.Node;
import parser.nodes.TerminalNode;
import parser.operators.NumericOperator;

public class TreeParser2 {
	
	TreeParser2(){
		
	}
	
	public static NicerTree parseTree(String str, Game game){
			
		TreeParser2 parser = new TreeParser2();
		
		List<String> strList = new ArrayList<String>(Arrays.asList(str.split("_")));
		Node root = parser.parseTree(strList, game);

		NicerTree tree = new NicerTree(root, game);		
		return tree;
	}
	
	private Node parseTree(List<String> strList, Game game){
		
		Node node = null;
		
		if(strList.get(0).equals("if(")){
			int closeParentesisIndex = findCloseParentesis(strList.subList(1, strList.size())) + 1;
			node = parseIfNode(strList.subList(1, closeParentesisIndex));
			
			int closeBracketIndex = findCloseBracketNumber(1, strList.subList(closeParentesisIndex+1, strList.size()), 0) + closeParentesisIndex+1;
			node.addChildren(parseTree(strList.subList(closeParentesisIndex+1, closeBracketIndex), game));
			
			int possibleElseIndex = closeBracketIndex + 1;
			if(possibleElseIndex < strList.size() && strList.get(possibleElseIndex).equals("else{")){
				closeBracketIndex = findCloseBracketNumber(1,strList.subList(possibleElseIndex + 2, strList.size()),0) + possibleElseIndex + 2;
				node.addChildren(parseTree(strList.subList(possibleElseIndex+1, closeBracketIndex), game));
				
			}
			
			if(closeBracketIndex+1 < strList.size()){
				System.err.println("Incomplete parse");
			}
		}
		else if (isTerminalFunc(strList.get(0)))
			node = new TerminalNode(getTerminalFunc(strList.get(0)));
		else
			System.out.println("Error: unknown selection clause");

		return node;
	}

	private IfNode parseIfNode(List<String> list){

		ArrayList<BooleanOperator> bopl = new ArrayList<>();
		ArrayList<Condition> cl = new ArrayList<>();
		
		int nextCondToAdd = 0;
		
		for (int i = 0; i < list.size(); i++) {
			String current = list.get(i);
			if(isBooleanOperator(current)){
				bopl.add(getBooleanOperator(current));
				cl.add(parseCondition(list.subList(nextCondToAdd, i)));
				nextCondToAdd = i + 1;
			}
		}
		
		cl.add(parseCondition(new ArrayList<>(list.subList(nextCondToAdd, list.size()))));	//adds the last Condition
		
		return new IfNode(cl, bopl);
	}
	
	private Condition parseCondition(List<String> strList){
		Condition cond = null;
		if (strList.size() == 1 && isBooleanFunc(strList.get(0))){
			cond = new Condition(getBooleanFunc(strList.get(0)));
		}
		else if (strList.size() == 3 && isNumericOperator(strList.get(1))) {	//type bool
			String num1 = strList.get(0);
			String num2 = strList.get(2);
			NumericOperator op = getNumericOperator(strList.get(1));
			
			if (isNumericFunc(num1) && isNumericFunc(num2)){					//type func_func
				NumericFunc nf1 = getNumericFunc(num1);
				NumericFunc nf2 = getNumericFunc(num2);
				cond = new Condition(nf1, nf2, op);
			}
			else if (isNumericFunc(num1)){										//type func_num
				NumericFunc nf = getNumericFunc(num1);
				int number = parseInt(num2);
				cond = new Condition(nf, number, op);
			}
			else if (isNumericFunc(num2)){										//type num_func
				NumericFunc nf = getNumericFunc(num2);
				int number = parseInt(num1);
				cond = new Condition(number, nf, op);
			}
		}
		return cond;
	}
	
	private boolean isBooleanOperator(String str) {
		return getBooleanOperator(str) != null;
	}
	
	private BooleanOperator getBooleanOperator(String str) {	
		BooleanOperator op = null;
		try {
			op = BooleanOperator.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return op;
	}
	
	private boolean isNumericOperator(String str) {
		return getNumericOperator(str) != null;
	}
	
	private NumericOperator getNumericOperator(String str) {	
		NumericOperator op = null;
		try {
			op = NumericOperator.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return op;
	}
	
	private boolean isBooleanFunc(String str) {
		return getBooleanFunc(str) != null;
	}
	
	private BooleanFunc getBooleanFunc(String str) {	
		BooleanFunc bf = null;
		try {
			bf = BooleanFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return bf;
	}
	
	private boolean isNumericFunc(String str) {
		return getNumericFunc(str) != null;
	}
	
	private NumericFunc getNumericFunc(String str) {	
		NumericFunc nf = null;
		try {
			nf = NumericFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return nf;
	}

	private boolean isTerminalFunc(String str) {
		return getTerminalFunc(str) != null;
	}
	
	private Action getTerminalFunc(String str) {	
		Action tf = null;
		try {
			tf = Action.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return tf;
	}
	
	private int parseInt(String str){
		return Integer.parseInt(str);
	}
	
	private int findCloseBracketNumber(int number, String[] str, int currentPos) {
		String currentStr = str[currentPos];
		
		if (currentStr.charAt(currentStr.length()-1) == '{')		//new opening bracket
			return findCloseBracketNumber(number+1, str, currentPos+1);
		else if (currentStr.charAt(0) == '}') {		//new closing bracket
			number--;
			if (number == 0)	//all brackets closed
				return currentPos;
			else		//more brackets to close
				return findCloseBracketNumber(number, str, currentPos+1);
		}
		else		//more brackets to close
			return findCloseBracketNumber(number, str, currentPos+1);
		
	}

	private int findCloseBracketNumber(int number, List<String> list, int currentPos) {
		String currentStr = list.get(0);
		
		if (currentStr.charAt(currentStr.length()-1) == '{')		//new opening bracket
			return findCloseBracketNumber(number+1, list.subList(1, list.size()), currentPos+1);
		else if (currentStr.charAt(0) == '}') {		//new closing bracket
			number--;
			if (number == 0)	//all brackets closed
				return currentPos;
			else		//more brackets to close
				return findCloseBracketNumber(number, list.subList(1, list.size()), currentPos+1);
		}
		else		//more brackets to close
			return findCloseBracketNumber(number, list.subList(1, list.size()), currentPos+1);
	}

	private int findCloseParentesis(String[] str, int currentPos) {
		
		for (int i = currentPos; i < str.length; i++){
			if(str[i].charAt(0) == ')')
				return i;
		}
		return -1;
	}

	private int findCloseParentesis(List<String> subList) {
		for (int i = 0; i < subList.size(); i++) {
			if(subList.get(i).charAt(0) == ')')
				return i;
		}
		return -1;
	}
}

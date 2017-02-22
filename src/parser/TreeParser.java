package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import parser.operators.BooleanOperator;
import parser.function.BooleanFunc;
import parser.function.NumericFunc;
import parser.function.Action;
import parser.nodes.IfNode;
import parser.nodes.NicerTree;
import parser.nodes.Node;
import parser.nodes.TerminalNode;
import parser.operators.NumericOperator;

public class TreeParser {
	
	
	public static NicerTree parseTree(String str){
					
		List<String> strList = new ArrayList<String>(Arrays.asList(str.split("_")));
		Node root = parseTree(strList);

		NicerTree tree = new NicerTree(root);		
		return tree;
	}
	
	private static Node parseTree(List<String> strList){
		
		Node node = null;
		
		if(strList.get(0).equals("if(")){
			int closeParentesisIndex = findCloseParentesis(strList.subList(1, strList.size())) + 1;
			node = parseIfNode(strList.subList(1, closeParentesisIndex));
			
			int closeBracketIndex = findCloseBracketNumber(1, strList.subList(closeParentesisIndex+1, strList.size()), 0) + closeParentesisIndex+1;
			node.addChildren(parseTree(strList.subList(closeParentesisIndex+1, closeBracketIndex)));
			
			int possibleElseIndex = closeBracketIndex + 1;
			if(possibleElseIndex < strList.size() && strList.get(possibleElseIndex).equals("else{")){
				closeBracketIndex = findCloseBracketNumber(1,strList.subList(possibleElseIndex + 2, strList.size()),0) + possibleElseIndex + 2;
				node.addChildren(parseTree(strList.subList(possibleElseIndex+1, closeBracketIndex)));
				
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
	
	public static String clean(String str){
		
		str = str.replaceAll("([ ]{2,})|(\t)|(\r)|(\n)", "");
		
		str = str.replace("&&", "and");
		str = str.replace("||", "or");

		str = str.replace("==", "EQ");
		str = str.replace("!=", "NE");
		str = str.replace("<=", "LE");	//always check before checking <
		str = str.replace(">=", "GE");	//always check before checking >
		str = str.replace("<", "LT");
		str = str.replace(">", "GT");
				
		str = introduceSpaces(str);
		
		str = str.replace(' ', '_');
		
		return str;
	}
	
	public static String introduceSpaces(String str) {
		str = str.replace("if (", "if(");
		str = str.replace(") {", "){");
		str = str.replaceAll("\\((?!\\_|\\ )", "( ");		//add space after "(" when neccesary
		str = str.replaceAll("\\{(?!\\_|\\ )", "{ ");		//add space after "{" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )\\)", " )");	//add space before ")" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )\\}", " }");	//add space before "}" when neccesary
		
		str = str.replaceAll("(\\}else\\{)|(\\}else\\ \\{)|(\\}\\ else\\ \\{)", "} else{");	//add space before "else{" when neccesary
		
		str = str.replaceAll("\\!(?!\\_|\\ )", "! ");		//add space after "!" when neccesary
		
		str = str.replaceAll("&&(?!\\_|\\ )", "&& ");				//add space after "&&" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )&&", " &&");			//add space before "&&" when neccesary
		str = str.replaceAll("\\|\\|(?!\\_|\\ )", "|| ");		//add space after "(" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )\\|\\|", " ||");		//add space before "&&" when neccesary
		

		str = str.replaceAll("EQ(?!\\_|\\ )", "EQ ");		//add space after "EQ" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )EQ", " EQ");	//add space before "EQ" when neccesary
		str = str.replaceAll("NE(?!\\_|\\ )", "NE ");		//add space after "EQ" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )NE", " NE");	//add space before "NE" when neccesary
		str = str.replaceAll("LT(?!\\_|\\ )", "LT ");		//add space after "LT" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )LT", " LT");	//add space before "LT" when neccesary
		str = str.replaceAll("GT(?!\\_|\\ )", "GT ");		//add space after "GT" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )GT", " GT");	//add space before "GT" when neccesary
		str = str.replaceAll("LE(?!\\_|\\ )", "LE ");		//add space after "LE" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )LE", " LE");	//add space before "LE" when neccesary
		str = str.replaceAll("GE(?!\\_|\\ )", "GE ");		//add space after "GE" when neccesary
		str = str.replaceAll("(?<!\\_|\\ )GE", " GE");	//add space before "GE" when neccesary
		
		return str;
	}

	private static IfNode parseIfNode(List<String> list){

		ArrayList<BooleanOperator> bopl = new ArrayList<>();
		ArrayList<Condition> cl = new ArrayList<>();
		
		List<String> nextCondToAdd = new ArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			String current = list.get(i);
			if(isBooleanOperator(current)){
				bopl.add(getBooleanOperator(current));
				cl.add(parseCondition(nextCondToAdd));
				nextCondToAdd.clear();
			}
			else {
				nextCondToAdd.add(current);
			}
		}
		
		cl.add(parseCondition(nextCondToAdd));	//adds the last Condition
		
		return new IfNode(cl, bopl);
	}
	
	private static Condition parseCondition(List<String> strList){
		Condition cond = null;
		boolean isNegated = false;
				
		if(strList.get(0).equals("!")){
			isNegated = true;
			strList.remove(0);
		}
					
		if (strList.size() == 1 && isBooleanFunc(strList.get(0))){
			cond = new Condition(getBooleanFunc(strList.get(0)), isNegated);
		}
		else if (strList.size() == 3 && isNumericOperator(strList.get(1))) {	//type bool
			String num1 = strList.get(0);
			String num2 = strList.get(2);
			NumericOperator op = getNumericOperator(strList.get(1));
			
			if (isNumericFunc(num1) && isNumericFunc(num2)){					//type func_func
				NumericFunc nf1 = getNumericFunc(num1);
				NumericFunc nf2 = getNumericFunc(num2);
				cond = new Condition(nf1, nf2, op, isNegated);
			}
			else if (isNumericFunc(num1)){										//type func_num
				NumericFunc nf = getNumericFunc(num1);
				int number = parseInt(num2);
				cond = new Condition(nf, number, op, isNegated);
			}
			else if (isNumericFunc(num2)){										//type num_func
				NumericFunc nf = getNumericFunc(num2);
				int number = parseInt(num1);
				cond = new Condition(number, nf, op, isNegated);
			}
		}
		return cond;
	}
	
	private static boolean isBooleanOperator(String str) {
		return getBooleanOperator(str) != null;
	}
	
	private static BooleanOperator getBooleanOperator(String str) {	
		BooleanOperator op = null;
		try {
			op = BooleanOperator.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return op;
	}
	
	private static boolean isNumericOperator(String str) {
		return getNumericOperator(str) != null;
	}
	
	private static NumericOperator getNumericOperator(String str) {	
		NumericOperator op = null;
		try {
			op = NumericOperator.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return op;
	}
	
	private static boolean isBooleanFunc(String str) {
		return getBooleanFunc(str) != null;
	}
	
	private static BooleanFunc getBooleanFunc(String str) {	
		BooleanFunc bf = null;
		try {
			bf = BooleanFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return bf;
	}
	
	private static boolean isNumericFunc(String str) {
		return getNumericFunc(str) != null;
	}
	
	private static NumericFunc getNumericFunc(String str) {	
		NumericFunc nf = null;
		try {
			nf = NumericFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return nf;
	}

	private static boolean isTerminalFunc(String str) {
		return getTerminalFunc(str) != null;
	}
	
	private static Action getTerminalFunc(String str) {	
		Action tf = null;
		try {
			tf = Action.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return tf;
	}
	
	private static int parseInt(String str){
		return Integer.parseInt(str);
	}

	private static int findCloseBracketNumber(int number, List<String> list, int currentPos) {
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

	private static int findCloseParentesis(List<String> subList) {
		for (int i = 0; i < subList.size(); i++) {
			if(subList.get(i).charAt(0) == ')')
				return i;
		}
		return -1;
	}
}

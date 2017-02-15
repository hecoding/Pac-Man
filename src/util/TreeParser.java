package util;

import treeProgram.function.BooleanFunc;
import treeProgram.function.NumberFunc;
import treeProgram.function.NumberFuncWrapper;
import treeProgram.function.TerminalFunc;
import treeProgram.operator.NumberOperator;
import treeProgram.Node;
import treeProgram.ProgramTree;

public class TreeParser {

	public TreeParser() {
		
	}
	
	public static ProgramTree toTree(String str) {
		return toTree(str.split(" "), 0);
	}

	static ProgramTree toTree(String[] strList, int currentPos) {
		
		ProgramTree tree = new ProgramTree();
		
		String currentStr = strList[currentPos];
		int closingBracketPos;
		
		if (currentStr.equals("if(")) {
			currentPos++;
			int closingParentesisPos = findCloseParentesis(strList, currentPos);
			Node cond = parseCondition(strList, currentPos, closingParentesisPos);
			tree.setValue(cond);
			currentPos = closingParentesisPos + 1;
			closingBracketPos = findCloseBracketNumber(1, strList, currentPos+1);
			tree.addChild(toTree(strList, currentPos+1));
			currentPos = closingBracketPos + 1;
			
			if(currentPos < strList.length) {	//not ended
				currentStr = strList[currentPos];
				if (currentStr.equals("else{")) {
					closingBracketPos = findCloseBracketNumber(1, strList, currentPos+1);
					tree.addChild(toTree(strList, currentPos+1));
					currentPos = closingBracketPos + 1;
				}
			}
		}
		
		else if (!isTerminalFunc(tree, currentStr))		// try to add terminal function
			System.out.println("unknown selection-statement");
		
		return tree;		
	}

	static int findCloseBracketNumber(int number, String[] str, int currentPos) {
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

	static int findCloseParentesis(String[] str, int currentPos) {
		
		for (int i = currentPos; i < str.length; i++){
			if(str[i].charAt(0) == ')')
				return i;
		}
		return -1;
	}

	static Node parseCondition(String[] str, int start, int end){	
		//TODO: Add '!' (not)
		//TODO: Add boolean operators

		BooleanFunc bf = getBooleanFunc(str[start]);	//boolean-func
		if (bf != null)
			return bf;
		
		if (start+3 == end){
			NumberFuncWrapper wrapper = getWrapper(str[start], str[start+1], str[start+2]);		//number-func
			if (wrapper != null)
				return wrapper;
		}
		
		System.err.println("condition unknown");
		return null;
		
	}

	static boolean isTerminalFunc(ProgramTree tree, String str){
			
		TerminalFunc terminal = null;
		try {
			terminal = TerminalFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}

		if (terminal != null){
			tree.setValue(terminal);
			return true;
		}
		else
			return false;
		
	}

	static BooleanFunc getBooleanFunc(String str){	
		BooleanFunc bf = null;
		try {
			bf = BooleanFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return bf;
	}
	
	static NumberFuncWrapper getWrapper(String nfStr, String opStr, String numStr){	
		
		NumberFuncWrapper wr = null;
		NumberFunc nf = null;
		try {
			 nf = NumberFunc.valueOf(nfStr);
		} catch (IllegalArgumentException e) {
		}
		
		if (nf != null){
			NumberOperator op = NumberOperator.stringToNumOperator(opStr);
			int num = Integer.parseInt(numStr);
			
			wr = new NumberFuncWrapper(nf, num, op);
		}
		
		return wr;
	}

}

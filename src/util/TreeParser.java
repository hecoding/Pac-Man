package util;

import treeProgram.function.BooleanFunc;
import treeProgram.function.NumberFunc;
import treeProgram.function.TerminalFunc;
import treeProgram.operator.NumberOperator;
import treeProgram.Node;

public class TreeParser {

	public TreeParser() {
		// TODO Auto-generated constructor stub
	}
	
	public Tree<Node> toTree(String str) {
		return toTree(str.split(" "), 0);
	}

	Tree<Node> toTree(String[] strList, int currentPos) {
		
		Tree<Node> tree = new Tree<Node>();
		
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
		
		else if (!isTerminalFunc(tree, currentStr))		//intenta a�adir la funcion terminal
			System.out.println("unknown selection-statement");
		
		/*switch (currentStr) {
				case "if(":		currentPos++;
								int closingParentesisPos = findCloseParentesis(strList, currentPos);
								TNode cond = parseCondition(strList, currentPos, closingParentesisPos);
								tree.setValue(cond);
								currentPos = closingParentesisPos+1;
								closingBracketPos = findCloseBracketNumber(1, strList, currentPos+1);
								tree.addChild(toTree(strList, currentPos+1, closingBracketPos));
								break;
				case "else{":	closingBracketPos = findCloseBracketNumber(1, strList, currentPos+1);
								tree.addChild(toTree(strList, currentPos+1, closingBracketPos));
								break;
				case "}":		//continue
								break;
				default:	if (!isTerminalFunc(tree, currentStr))
									//if(!isBooleanFunc(tree, currentStr))
								break;
		}*/
		
		return tree;		
	}

	int findCloseBracketNumber(int number, String[] str, int currentPos) {
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

	int findCloseParentesis(String[] str, int currentPos) {
		
		for (int i = currentPos; i < str.length; i++){
			if(str[i].charAt(0) == ')')
				return i;
		}
		return -1;
	}

	Node parseCondition(String[] str, int start, int end){	
		//TODO: Add '!' (not)
		//TODO: Add boolean operators

		BooleanFunc bf = getBooleanFunc(str[start]);	//boolean-func
		if (bf != null)
			return bf;
		
		if (start+3 == end){
			NumberFunc nf = getNumberFunc(str[start], str[start+1], str[start+2]);		//number-func
			if (nf != null)
				return nf;
		}
		
		System.err.println("condition unknown");
		return null;
		
	}

	boolean isTerminalFunc(Tree<Node> tree, String str){
			
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

	BooleanFunc getBooleanFunc(String str){	
		BooleanFunc bf = null;
		try {
			bf = BooleanFunc.valueOf(str);
		} catch (IllegalArgumentException e) {
		}
		return bf;
	}
	
	NumberFunc getNumberFunc(String nfStr, String opStr, String numStr){	
		
		NumberFunc nf = null;
		try {
			 nf = NumberFunc.valueOf(nfStr);
		} catch (IllegalArgumentException e) {
		}
		
		if (nf != null){
			NumberOperator op = NumberOperator.stringtoNumOperator(opStr);
			int num = Integer.parseInt(numStr);
			nf.setnOP(op);
			nf.setNumber(num);
		}
		
		return nf;
	}

}

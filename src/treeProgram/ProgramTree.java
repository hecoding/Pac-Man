package treeProgram;

import treeProgram.function.NumberFuncWrapper;
import util.Tree;

public class ProgramTree extends Tree<Node> {
	
	public String horizontalPreOrder(){
		String val = "";
		if(this.value == null)
			return val;
		
		val = this.value.toString();
		
		if(this.isLeaf())
			return val;
		else {
			String nextChildren = "{ " + printVal(this.value);
			for (Tree<Node> child : children) {
				if(child == null)
					nextChildren += " null";
				else
					nextChildren += " " + child.horizontalPreOrder();
			}
			return nextChildren + " }";
		}
	}
	
	public String printVal(Node val) {
		String stringVal = "";
		
		stringVal += "if( ";
		
		if(val instanceof NumberFuncWrapper) {
			NumberFuncWrapper realVal = (NumberFuncWrapper) val;
			stringVal += realVal.toString();
		}
		
		stringVal += " )";
		
		return stringVal;
	}
	
	public String toString() {
		return this.horizontalPreOrder();
	}

}

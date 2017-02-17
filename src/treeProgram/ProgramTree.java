package treeProgram;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import treeProgram.function.BooleanFunc;
import treeProgram.function.Function;
import treeProgram.function.NumberFunc;
import treeProgram.operator.NumberOperator;
import treeProgram.wrapper.NumberFuncWrapper;
import util.Tree;

public class ProgramTree extends Tree<Node> {
	
	public MOVE execute(Game game) {
		return executeRecursive(this, game);
	}
	
	private static MOVE executeRecursive(ProgramTree program, Game game) {
		Node currentVal = program.getValue();
		
		if(!(currentVal instanceof Terminal)) {
			// functions
			Function currentFunc = (Function) currentVal;
			
			if(executeCondition(currentFunc, game)) {
				return executeRecursive((ProgramTree) program.getLeftChild(), game);
			}
			else {
				if(program.getNumChildren() == 2) // if has else
					return executeRecursive((ProgramTree) program.getRightChild(), game);
			}
			
			return null;
		}
		else {
			// terminals
			if(currentVal == Terminal.comer) {
				int pacmanPos = game.getPacmanCurrentNodeIndex();
				int closestGhostPos = game.getClosestNonEdibleGhost(pacmanPos).currentNodeIndex;
				
				return game.getNextMoveAwayFromTarget(pacmanPos, closestGhostPos, DM.EUCLID); // por poner una funcion
			}
			
			return null;
		}
	}
	
	public static boolean executeCondition(Function fun, Game game) {
		if(fun instanceof NumberFunc) {
			NumberFuncWrapper funcWrap = (NumberFuncWrapper) fun;
			NumberFunc func = funcWrap.getNf();
			NumberOperator opFunc = funcWrap.getnOP();
			int numFunc = funcWrap.getNumber();
			
			if(func == NumberFunc.distancia) {
				int pacmanPos = game.getPacmanCurrentNodeIndex();
				int dist = game.getDistanceToClosestNonEdibleGhost(pacmanPos); // por poner una
				
				return opFunc.evaluateOperator(dist, numFunc);
			}
		}
		else if(fun instanceof BooleanFunc) {
			BooleanFunc func = (BooleanFunc) fun;
			
			if(func == BooleanFunc.peligro) {
				return game.isJunction(0);  // por poner una
			}
		}
		
		return false;
	}
	
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

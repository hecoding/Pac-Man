package pacman.controllers;

import java.util.Stack;

import alg.program.Function;
import alg.program.Node;
import alg.program.Terminal;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import util.Tree;

public class GeneticController extends Controller<MOVE> {
	private Tree<Node> program;
	private Tree<Node> currentNode;
	private Stack<Tree<Node>> stack;
	
	public GeneticController(Tree<Node> program) {
		super();
		this.program = program;
		this.currentNode = program;
		this.stack = new Stack<>();
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		return runProgram(game);
	}
	
	private MOVE runProgram(Game game) {
		MOVE result = null;
	
		while(result == null ) {
			
			// functions
			if(currentNode.getValue() == Function.progn3) {
				for (int i = Function.numberOfChildren(Function.progn3) - 1; i >= 0; i--) {
					this.stack.add(currentNode.getNChild(i));
				}
			}
			else if(currentNode.getValue() == Function.progn2) {
				this.stack.add(currentNode.getRightChild());
				this.stack.add(currentNode.getLeftChild());
			}
			else if(currentNode.getValue() == Function.ifPillAhead) {
				int ahead = game.getNeighbour(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
				if(ahead == -1 || game.isPillStillAvailable(ahead))
					this.stack.add(currentNode.getLeftChild());
				else
					this.stack.add(currentNode.getRightChild());
			}
			// terminals
			else if(currentNode.getValue() == Terminal.up)
				result = MOVE.UP;
			else if(currentNode.getValue() == Terminal.down)
				result = MOVE.DOWN;
			else if(currentNode.getValue() == Terminal.right)
				result = MOVE.RIGHT;
			else if(currentNode.getValue() == Terminal.left)
				result = MOVE.LEFT;
			
			// get next node of the tree or start from the beginning
			if(!stack.isEmpty())
				this.currentNode = stack.pop();
			else
				this.currentNode = this.program;
		}
		
		return result;
	}

}

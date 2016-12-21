package pacman.controllers;

import java.util.Stack;

import alg.program.Function;
import alg.program.Node;
import alg.program.Terminal;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;
import util.Tree;

public class GeneticController extends Controller<MOVE> {
	private Tree<Node> program;
	private Tree<Node> currentNode;
	private Stack<Tree<Node>> stack;
	private static final int PANIC_DISTANCE = 5;
	private static final int HUNGER_DISTANCE = 30;
	private char bucleState = 'N';
	
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
		int currentPos = game.getPacmanCurrentNodeIndex();
		
		if(bucleState != 'N'){
			Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos); 
			if(closestNonEdibleGhost == null)
				bucleState = 'N';
			else if (!game.closerThan(currentPos, closestNonEdibleGhost.currentNodeIndex, PANIC_DISTANCE)){
				bucleState = 'N';
			}
		}
		
		if(bucleState != 'N') {
			this.currentNode = this.currentNode.getParent();
		}
	
		while(result == null ) {
			
			// functions
			if(currentNode.getValue() == Function.P) {
				Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos); 
				if(closestNonEdibleGhost != null && game.closerThan(currentPos, closestNonEdibleGhost.currentNodeIndex, PANIC_DISTANCE)) 
					this.stack.add(currentNode.getLeftChild());
				else
					this.stack.add(currentNode.getRightChild());
			}
			else if(currentNode.getValue() == Function.B) {
				Ghost closestEdibleGhost = game.getClosestEdibleGhost(currentPos); 
				//Ghost closestEdibleGhost = game.getClosestReachableEdibleGhost(currentPos);
				if(closestEdibleGhost != null && game.closerThan(currentPos, closestEdibleGhost.currentNodeIndex, HUNGER_DISTANCE))
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
			else if(currentNode.getValue() == Terminal.H) {
				Ghost closestNonEdibleGhost = game.getClosestNonEdibleGhost(currentPos);
				if(closestNonEdibleGhost != null){
					//myMove = game.getNextMoveAwayFromTarget(currentPos, closestNonEdibleGhost.currentNodeIndex, DM.PATH); //old escape function
					result = game.getNextMoveAwayFromTargetUpgraded(currentPos, closestNonEdibleGhost.currentNodeIndex, DM.PATH); //new escape function
					bucleState = 'H';
				}
			}
			else if(currentNode.getValue() == Terminal.C) {
				int closestPillOrPowerPill = game.getClosestPillOrPowerPill(currentPos);
				result = game.getNextMoveTowardsTarget(currentPos, closestPillOrPowerPill, DM.PATH);
			}
			else if(currentNode.getValue() == Terminal.E) {
				int closestPill = game.getClosestPill(currentPos);
				if (closestPill != -1)
					result = game.getNextMoveTowardsTarget(currentPos, closestPill, DM.PATH);
			}
			else if(currentNode.getValue() == Terminal.W) {
				int closestPowerPill = game.getClosestPowerPill(currentPos);
				if (closestPowerPill != -1)
					result = game.getNextMoveTowardsTarget(currentPos, closestPowerPill, DM.PATH);
			}
			else if(currentNode.getValue() == Terminal.F) {
				Ghost closestEdibleGhost = game.getClosestEdibleGhost(currentPos);
				//Ghost closestEdibleGhost = game.getClosestReachableEdibleGhost(currentPos);
				if(closestEdibleGhost != null)
					result = game.getNextMoveTowardsTarget(currentPos, closestEdibleGhost.currentNodeIndex, game.getPacmanLastMoveMade(), DM.PATH);
			}
			
			// get next node of the tree or start from the beginning
			if(!stack.isEmpty())
				this.currentNode = stack.pop();
			else
				this.reset();
		}
		
		return result;
	}
	
	public void reset(){
		this.currentNode = this.program;
	}

}

package parser.nodes;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import parser.function.Action;

public class TerminalNode extends Node {
	private Action function;
	
	public TerminalNode(Action f){
		function = f;
	}

	public Node execute(Game g) {
		return this;
	}
		
	public MOVE getMove(Game g){
		return function.getMove(g);
	}
	
	public String toString(){
		return function.toString();
	}
	
	public String pretty(String tabs){
		return tabs + this.toString();
	}
}

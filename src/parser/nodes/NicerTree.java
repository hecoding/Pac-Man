package parser.nodes;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class NicerTree {

	private Node root;
	private Game game;
	
	public NicerTree(Node r, Game g) {
		root = r;
		game = g;
	}
	
	public Node execute(){
		return root.execute(game);
	}
	
	public MOVE executeAndGetMove(Game g){
		game = g;
		Node tn = this.execute();
		if (tn == null)
			return MOVE.NEUTRAL;
		if (tn.children.size() != 0){
			System.out.println();
		}
		return ((TerminalNode) this.execute()).getMove(game);
	}
	
	public String toString(){
		return root.toString();
	}
	
	public String pretty(){
		return root.pretty("");
	}
}

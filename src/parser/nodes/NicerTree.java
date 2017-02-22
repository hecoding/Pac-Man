package parser.nodes;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class NicerTree {

	private Node root;
	
	public NicerTree(Node r) {
		root = r;
	}
	
	public Node execute(Game game){
		return root.execute(game);
	}
	
	public MOVE executeAndGetMove(Game g){
		Node tn = this.execute(g);
		if (tn == null)
			return MOVE.NEUTRAL;
		if (tn.children.size() != 0){
			System.out.println();
		}
		return ((TerminalNode) this.execute(g)).getMove(g);
	}
	
	public String toString(){
		return root.toString();
	}
	
	public String pretty(){
		return root.pretty("");
	}
}

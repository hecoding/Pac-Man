package parser.nodes;

import java.util.ArrayList;
import pacman.game.Game;

public abstract class Node {
	
	public static int DEFAULT_CHILDREN_NO = 2;
	
	protected ArrayList<Node> children;
	protected Node parent;
	
	Node(){
		children = new ArrayList<>(DEFAULT_CHILDREN_NO);
		parent = null;
	}

	private void setParent(Node p){
		this.parent = p;
	}
	
	public void addChildren(Node c){
		this.children.add(c);
		c.setParent(this);
	}

	public ArrayList<Node> getChildren(){
		return this.children;
	}
	
	public Node getParent(){
		return this.parent;
	}

	public abstract Node execute(Game g);
	public abstract String pretty(String tabs);

}

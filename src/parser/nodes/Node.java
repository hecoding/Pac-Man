package parser.nodes;

import java.util.ArrayList;
import java.util.Iterator;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public abstract class Node {
	
	protected ArrayList<Node> children;
	protected Node parent;
	
	Node(){
		children = new ArrayList<>();
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
	
	public ArrayList<Node> getParent(){
		return this.children;
	}

	public abstract Node execute(Game g);

}

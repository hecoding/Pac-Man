package util.nodeGenerator;

import treeProgram.Node;
import util.Tree;

public class NodeGeneratorImp implements NodeGenerator<Node> {
	private static NodeGeneratorImp instance;
	
	private NodeGeneratorImp() {}
	
	public static NodeGeneratorImp getInstance() {
		if (instance == null){
			instance = new NodeGeneratorImp();
		}
		return instance;
	}

	@Override
	public Tree<Node> next() {
		Tree<Node> terminal = new Tree<Node>();
		
		return terminal;
	}

}
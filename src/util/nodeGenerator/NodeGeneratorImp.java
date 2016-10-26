package util.nodeGenerator;

import model.program.Node;
import model.program.Terminal;
import util.RandGenerator;
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
		terminal.setValue(Terminal.values()[RandGenerator.getInstance().nextInt(Terminal.values().length)]);
		
		return terminal;
	}

}

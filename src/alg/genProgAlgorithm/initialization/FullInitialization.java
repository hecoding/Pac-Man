package model.genProgAlgorithm.initialization;

import model.program.Function;
import model.program.Node;
import model.program.Terminal;
import util.RandGenerator;
import util.Tree;

public class FullInitialization extends InitializationAbstract {
	
	public void initialize(Tree<Node> program, int programLevels) {
		if(programLevels == 1)
			program.setValue(Terminal.values()[RandGenerator.getInstance().nextInt(Terminal.values().length)]);
		
		else {
			program.setValue(Function.values()[RandGenerator.getInstance().nextInt(Function.values().length)]);
			
			for (int i = 0; i < Function.numberOfChildren(program.getValue()); i++) {
				Tree<Node> child = new Tree<>();
				child.setParent(program);
				initialize(child, programLevels - 1);
				program.addChild(child);
			}
		}
	}

	@Override
	public String getName() {
		return "Full";
	}

}

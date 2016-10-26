package util.nodeGenerator;

import util.Tree;

public interface NodeGenerator<T> {
	public Tree<T> next();
}

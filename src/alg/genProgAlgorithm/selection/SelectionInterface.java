package alg.genProgAlgorithm.selection;

import java.util.ArrayList;
import alg.chromosome.AbstractChromosome;

public interface SelectionInterface {
	public <T extends AbstractChromosome> void select(ArrayList<T> population);
	public String getName();
}

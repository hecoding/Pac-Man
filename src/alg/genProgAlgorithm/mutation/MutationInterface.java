package alg.genProgAlgorithm.mutation;

import java.util.ArrayList;
import alg.chromosome.AbstractChromosome;

public interface MutationInterface {
	public <T extends AbstractChromosome> void mutate(ArrayList<T> population, double mutationProb);
	public String getName();
}

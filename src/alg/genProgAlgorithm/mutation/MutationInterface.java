package model.genProgAlgorithm.mutation;

import java.util.ArrayList;
import model.chromosome.AbstractChromosome;

public interface MutationInterface {
	public <T extends AbstractChromosome> void mutate(ArrayList<T> population, double mutationProb);
	public String getName();
}

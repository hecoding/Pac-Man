package alg.genProgAlgorithm.crossover;

import java.util.ArrayList;
import alg.chromosome.AbstractChromosome;

public interface CrossoverInterface {
	public <T extends AbstractChromosome> void crossover(ArrayList<T> population, double crossProb);
	public String getName();
}

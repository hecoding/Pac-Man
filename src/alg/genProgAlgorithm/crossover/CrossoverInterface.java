package model.genProgAlgorithm.crossover;

import java.util.ArrayList;
import model.chromosome.AbstractChromosome;

public interface CrossoverInterface {
	public <T extends AbstractChromosome> void crossover(ArrayList<T> population, double crossProb);
	public String getName();
}

package alg.genProgAlgorithm.crossover;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import alg.chromosome.AbstractChromosome;

public interface CrossoverInterface {
	/*
	 * It is worth having in mind that the population the method receive is not the total amount
	 * because each available CPU is given a slice to run in parallel for efficiency purposes.
	 * 
	 * Due to the parallelization you should also use the random generator instance that is provided by parameter
	 * (java.util.concurrent.ThreadLocalRandom) instead of using java.util.random or Math.random
	 * to minimize overhead and contention.
	 */
	public <T extends AbstractChromosome> void crossover(List<T> population, double crossProb, ThreadLocalRandom random);
	public String getName();
}

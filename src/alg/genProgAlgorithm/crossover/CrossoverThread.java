package alg.genProgAlgorithm.crossover;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import alg.chromosome.AbstractChromosome;

public class CrossoverThread<T extends AbstractChromosome> extends Thread {
	List<T> population;
	double crossProb;
	CrossoverInterface crossoverStrategy;
	ThreadLocalRandom random;
	
	public CrossoverThread(List<T> population, double crossProb, CrossoverInterface crossoverStrategy) {
		this.population = population;
		this.crossProb = crossProb;
		this.crossoverStrategy = crossoverStrategy;
		this.random = ThreadLocalRandom.current();
	}
	
	public void run() {
		this.crossoverStrategy.crossover(this.population, this.crossProb, this.random);
	}

}

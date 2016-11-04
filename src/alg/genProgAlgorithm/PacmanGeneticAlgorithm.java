package alg.genProgAlgorithm;

import alg.chromosome.PacmanChromosome;
import alg.genProgAlgorithm.crossover.CrossoverInterface;
import alg.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import alg.genProgAlgorithm.initialization.InitializationInterface;
import alg.genProgAlgorithm.mutation.MutationInterface;
import alg.genProgAlgorithm.selection.SelectionInterface;

public class PacmanGeneticAlgorithm extends AbstractGeneticAlgorithm<PacmanChromosome> {

	public PacmanGeneticAlgorithm(InitializationInterface initializationStrategy, FitnessFunctionInterface func,
			SelectionInterface selectionStrategy, CrossoverInterface crossoverStrategy, MutationInterface mutationStrategy,
			int populationNum, boolean useElitism, double elitePercentage, int maxGenerationNum, int maxProgramHeight,
			double crossProb, double mutationProb) {
		super(func, initializationStrategy, selectionStrategy, crossoverStrategy, mutationStrategy, populationNum,
				useElitism, elitePercentage, maxGenerationNum, maxProgramHeight, crossProb, mutationProb);
	}
	
	public String toString() {
		if(this.population == null || this.population.size() == 0) return "";
		String s = new String();
		
		s += "pos aptitude phenotype" + System.lineSeparator();
		for (int i = 0; i < this.population.size(); i++) {
			PacmanChromosome elem = this.population.get(i);
			s += i + " " +
				 String.format("%5f", elem.getAptitude()) + " " +
				 elem.getPhenotype() + System.lineSeparator();
		}
		
		return s;
	}

}

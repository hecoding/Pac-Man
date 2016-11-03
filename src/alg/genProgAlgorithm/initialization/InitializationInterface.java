package alg.genProgAlgorithm.initialization;

import java.util.ArrayList;

import alg.chromosome.AbstractChromosome;
import alg.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;

public interface InitializationInterface {
	public <T extends AbstractChromosome> ArrayList<T> initialize(int populationSize, FitnessFunctionInterface function, int programHeight);
	public String getName();
}

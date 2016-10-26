package model.genProgAlgorithm.initialization;

import java.util.ArrayList;

import model.chromosome.AbstractChromosome;
import model.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;

public interface InitializationInterface {
	public <T extends AbstractChromosome> ArrayList<T> initialize(int populationSize, FitnessFunctionInterface function, int programHeight);
	public String getName();
}

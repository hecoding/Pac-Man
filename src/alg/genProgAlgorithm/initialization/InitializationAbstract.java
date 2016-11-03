package alg.genProgAlgorithm.initialization;

import java.util.ArrayList;

import alg.chromosome.AbstractChromosome;
import alg.chromosome.AntTrailChromosome;
import alg.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import alg.program.Node;
import util.Tree;

public abstract class InitializationAbstract implements InitializationInterface {
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractChromosome> ArrayList<T> initialize(int populationSize, FitnessFunctionInterface function, int programHeight) {
		ArrayList<AntTrailChromosome> population = new ArrayList<AntTrailChromosome>(populationSize);
		for (int i = 0; i < populationSize; i++)  {
			AntTrailChromosome chromosome = new AntTrailChromosome(function, programHeight);
			Tree<Node> program = new Tree<>();
			initialize(program, programHeight);
			
			chromosome.setProgram(program);
			chromosome.setAptitude(chromosome.evaluate());
			population.add(chromosome);
		}
		
		return (ArrayList<T>) population;
	}
	
	public abstract void initialize(Tree<Node> program, int programLevels);

	@Override
	public abstract String getName();
}

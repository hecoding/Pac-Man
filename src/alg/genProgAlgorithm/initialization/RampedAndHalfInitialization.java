package model.genProgAlgorithm.initialization;

import java.util.ArrayList;

import model.chromosome.AbstractChromosome;
import model.chromosome.AntTrailChromosome;
import model.genProgAlgorithm.fitnessFunction.FitnessFunctionInterface;
import model.program.Node;
import util.Tree;

public class RampedAndHalfInitialization implements InitializationInterface {
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractChromosome> ArrayList<T> initialize(int populationSize, FitnessFunctionInterface function, int programHeight) {
		ArrayList<AntTrailChromosome> population = new ArrayList<AntTrailChromosome>(populationSize);
		int chromsPerGroup = populationSize / programHeight;
		GrowInitialization growInit = new GrowInitialization();
		FullInitialization fullInit = new FullInitialization();
		
		for (int i = 1; i <= programHeight; i++)  { // for each group
			for (int j = 0; j < chromsPerGroup / 2 ; j++) { // half the group with grow
				AntTrailChromosome chromosome = new AntTrailChromosome(function, programHeight);
				Tree<Node> program = new Tree<>();
				
				growInit.initialize(program, i);
				
				chromosome.setProgram(program);
				chromosome.setAptitude(chromosome.evaluate());
				population.add(chromosome);
			}
			for (int j = chromsPerGroup / 2; j < chromsPerGroup; j++) { // half wit full
				AntTrailChromosome chromosome = new AntTrailChromosome(function, programHeight);
				Tree<Node> program = new Tree<>();
				
				fullInit.initialize(program, i);
				
				chromosome.setProgram(program);
				chromosome.setAptitude(chromosome.evaluate());
				population.add(chromosome);
			}
			
		}
		
		int remainder = populationSize % programHeight;
		if(remainder != 0) { // if the division isn't exact
			for (int j = 0; j < remainder / 2 ; j++) { // half the group with grow
				AntTrailChromosome chromosome = new AntTrailChromosome(function, programHeight);
				Tree<Node> program = new Tree<>();
				
				growInit.initialize(program, programHeight);
				
				chromosome.setProgram(program);
				chromosome.setAptitude(chromosome.evaluate());
				population.add(chromosome);
			}
			for (int j = remainder / 2; j < chromsPerGroup; j++) { // half wit full
				AntTrailChromosome chromosome = new AntTrailChromosome(function, programHeight);
				Tree<Node> program = new Tree<>();
				
				fullInit.initialize(program, programHeight);
				
				chromosome.setProgram(program);
				chromosome.setAptitude(chromosome.evaluate());
				population.add(chromosome);
			}
		}
		
		return (ArrayList<T>) population;
	}

	@Override
	public String getName() {
		return "Ra. & Half";
	}

}

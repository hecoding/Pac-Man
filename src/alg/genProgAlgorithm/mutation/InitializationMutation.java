package alg.genProgAlgorithm.mutation;

import java.util.ArrayList;

import alg.chromosome.AbstractChromosome;
import alg.chromosome.AntTrailChromosome;
import alg.genProgAlgorithm.initialization.GrowInitialization;
import alg.program.Node;
import util.RandGenerator;
import util.Tree;

public class InitializationMutation implements MutationInterface {
	@Override
	public <T extends AbstractChromosome> void mutate(ArrayList<T> population, double mutationProb) {
		RandGenerator random = RandGenerator.getInstance();
		
		for (T chrom : population) {
			if(random.nextDouble() < mutationProb) {
				mutate(chrom, mutationProb);
				chrom.setAptitude(chrom.evaluate());
			}
		}
	}
	
	public <T extends AbstractChromosome> void mutate(T chrom, double mutationProb) {
		
		Tree<Node> node = ((AntTrailChromosome) chrom).getProgram().getRandomNode();
		node.dropChilds();
		
		GrowInitialization initialization = new GrowInitialization();
		initialization.initialize(node, AntTrailChromosome.maxHeight);
		
		((AntTrailChromosome) chrom).trimProgram();
	}

	@Override
	public String getName() {
		return "Initialisation";
	}
}

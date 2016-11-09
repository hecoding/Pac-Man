package alg.genProgAlgorithm.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import alg.chromosome.AbstractChromosome;
import alg.chromosome.PacmanChromosome;
import alg.program.Node;
import util.Tree;

public class OnePointCrossover implements CrossoverInterface {
	ThreadLocalRandom random;

	@Override
	public <T extends AbstractChromosome> void crossover(List<T> population, double crossProb, ThreadLocalRandom random) {
		this.random = random;
		ArrayList<Integer> selectedCandidatesIdx = new ArrayList<Integer>(population.size());
		
		// select randomly all the candidates for the crossover
		for (int i = 0; i < population.size(); i++) {
			if(random.nextDouble() < crossProb)
				selectedCandidatesIdx.add(i);
		}
		
		// make size even
		if((selectedCandidatesIdx.size() % 2) == 1)
			selectedCandidatesIdx.remove(selectedCandidatesIdx.size() - 1);
		
		// iterate over pairs
		for (int i = 0; i < selectedCandidatesIdx.size(); i+=2) {
			T parent1 = population.get(selectedCandidatesIdx.get(i));
			T parent2 = population.get(selectedCandidatesIdx.get(i + 1));
			cross(population, parent1, parent2);
		}
	}

	private <T extends AbstractChromosome> void cross(List<T> population, T parent1, T parent2) {
		
		Tree<Node> parent1Gene = ((PacmanChromosome) parent1).getProgram();
		Tree<Node> parent2Gene = ((PacmanChromosome) parent2).getProgram();
		
		int nodesNum = 0;
		int nodes1 = parent1Gene.getNodes();
		int nodes2 = parent2Gene.getNodes();
		
		if(nodes1 != 1 && nodes2 != 1) {
			
			if(nodes1 < nodes2) nodesNum = nodes1;
			else nodesNum = nodes2;
			int crossNode = random.nextInt(nodesNum - 1) + 1; // all but the root
			
			Tree<Node> cross1 = parent1Gene.getNode(crossNode);
			Tree<Node> cross2 = parent2Gene.getNode(crossNode);
			Tree<Node> cross1parent = cross1.getParent();
			Tree<Node> cross2parent = cross2.getParent();
			
			cross1.getParent().exchangeChild(cross1, cross2);
			cross2.getParent().exchangeChild(cross2, cross1);
			cross1.setParent(cross2parent);
			cross2.setParent(cross1parent);
			
			((PacmanChromosome) parent1).trimProgram();
			((PacmanChromosome) parent2).trimProgram();
			
			parent1.setAptitude(parent1.evaluate());
			parent2.setAptitude(parent2.evaluate());
			
		}
	}

	@Override
	public String getName() {
		return "One-point";
	}

}

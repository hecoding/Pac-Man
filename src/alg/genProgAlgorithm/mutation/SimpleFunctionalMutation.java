package model.genProgAlgorithm.mutation;

import java.util.ArrayList;

import model.chromosome.AbstractChromosome;
import model.chromosome.AntTrailChromosome;
import model.program.Function;
import model.program.Node;
import util.RandGenerator;
import util.Tree;

public class SimpleFunctionalMutation implements MutationInterface {
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
		RandGenerator random = RandGenerator.getInstance();
		boolean finish = false;
		Tree<Node> inner;
		Tree<Node> program = ((AntTrailChromosome) chrom).getProgram();
		
		if(!program.isLeaf() && program.getNodes() != 4) {

		// progn3 is the only 3-children function so it can't mutate into another with same number of children
		int i = 0;
		do {
			inner = program.getRandomInnerNode();
			i++;
			if(i >= 50) {
				// overpopulated prog3 tree, turn into prog2
				inner.setValue(Function.progn2);
				Tree<Node> a = inner.getNChild(0);
				Tree<Node> b = inner.getNChild(1);
				inner.dropChilds();
				inner.addChild(a);
				inner.addChild(b);
			}
		} while (inner.getValue().equals(Function.progn3));
		i = 0;
		
			Function innerFunction = (Function) inner.getValue();
			Function newFunction;
			do {
				newFunction = Function.values()[random.nextInt(Function.values().length)];
				if (!innerFunction.equals(newFunction) && Function.numberOfChildren(innerFunction) == Function.numberOfChildren(newFunction)) {
					inner.setValue(newFunction);
					finish = true;
				}
			} while(!finish);
		
		}
		
	}

	@Override
	public String getName() {
		return "Smp. functional";
	}
}

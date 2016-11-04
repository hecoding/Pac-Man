package alg.genProgAlgorithm.mutation;

import java.util.ArrayList;

import alg.chromosome.AbstractChromosome;
import alg.chromosome.PacmanChromosome;
import alg.program.Node;
import alg.program.Terminal;
import util.RandGenerator;
import util.Tree;

public class SimpleTerminalMutation implements MutationInterface {
	
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

		Tree<Node> terminal = ((PacmanChromosome) chrom).getProgram().getRandomLeaf();
		Terminal newTerminal;
		do {
			newTerminal = Terminal.values()[random.nextInt(Terminal.values().length)];
			if (!terminal.getValue().equals(newTerminal)) {
				terminal.setValue(newTerminal);
				finish = true;
			}
		} while(!finish);
		
	}

	@Override
	public String getName() {
		return "Smp. terminal";
	}
}

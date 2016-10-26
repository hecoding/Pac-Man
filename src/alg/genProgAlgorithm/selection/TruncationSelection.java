package model.genProgAlgorithm.selection;

import java.util.ArrayList;

import model.chromosome.AbstractChromosome;
import model.chromosome.comparator.AptitudeComparator;

public class TruncationSelection implements SelectionInterface {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractChromosome> void select(ArrayList<T> population) {
		ArrayList<T> selectedPopulation = new ArrayList<T>(population.size());
		double truncProb = 0.1;
		int numSelectedElements = (int) (truncProb * 100);
		int timesEachSelected = (int) (1/truncProb);
		
		population.sort(new AptitudeComparator());
		
		for (int i = 0; i < numSelectedElements; i++) {
			for (int j = 0; j < timesEachSelected; j++) {
				selectedPopulation.add((T) population.get(i).clone());
			}
		}
		
		population.clear();
		population.addAll(selectedPopulation);
	}

	@Override
	public String getName() {
		return "Truncation";
	}

}

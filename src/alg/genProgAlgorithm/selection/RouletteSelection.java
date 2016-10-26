package model.genProgAlgorithm.selection;

import java.util.ArrayList;
import model.chromosome.AbstractChromosome;
import util.RandGenerator;

public class RouletteSelection implements SelectionInterface {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractChromosome> void select(ArrayList<T> population) {
		ArrayList<T> selectedPopulation = new ArrayList<T>(population.size());
		double prob = 0;
		int positionSelected = 0;
		RandGenerator random = RandGenerator.getInstance();
		
		for (int i = 0; i < population.size(); i++) {
			prob = random.nextDouble();
			positionSelected = 0;
			
			while((positionSelected < population.size()) &&
					(prob > population.get(positionSelected).getAggregateScore()))
				positionSelected++;
			
			selectedPopulation.add((T) population.get(positionSelected).clone());
		}
		
		population.clear();
		population.addAll(selectedPopulation);
	}

	@Override
	public String getName() {
		return "Roulette";
	}

}

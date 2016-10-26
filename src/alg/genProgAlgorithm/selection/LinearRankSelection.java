package model.genProgAlgorithm.selection;

import java.util.ArrayList;

import model.chromosome.AbstractChromosome;
import util.RandGenerator;

public class LinearRankSelection implements SelectionInterface {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractChromosome> void select(ArrayList<T> population) {
		ArrayList<T> selectedPopulation = new ArrayList<T>(population.size());
		double prob = 0;
		int positionSelected = 0;
		RandGenerator random = RandGenerator.getInstance();
		double beta = 1.5;
		int n = population.size();
		
		for (int i = 0; i < population.size(); i++) {
			prob = random.nextDouble();
			positionSelected = 0;
			double rankProb = (beta - (2.0 * (beta - 1.0) * ((i - 1)/(n - 1.0)) ))/n;
			System.out.println(prob);
			System.out.println(rankProb);
			
			while((positionSelected < population.size()-1) &&
					(prob > rankProb ))
				positionSelected++;
			
			System.out.println(positionSelected);
			selectedPopulation.add((T) population.get(positionSelected).clone());
		}
		
		population.clear();
		population.addAll(selectedPopulation);
	}

	@Override
	public String getName() {
		return "Linear Rank";
	}

}

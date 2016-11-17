package alg.genProgAlgorithm.fitnessFunction;

import java.util.ArrayList;

public class PacmanFitness implements FitnessFunctionInterface {

	@Override
	public double f(ArrayList<Double> params) {
		double score = params.get(0);
		double time = params.get(1);
		
		return score - time;
	}

	@Override
	public boolean isMinimization() {
		return false;
	}

	@Override
	public String getName() {
		return "PacMan Fitness";
	}

}

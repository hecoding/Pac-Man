package alg.genProgAlgorithm.fitnessFunction;

import java.util.ArrayList;

public class PacmanFitness implements FitnessFunctionInterface {

	@Override
	public double f(ArrayList<Double> params) {
		double score = params.get(0);
		
		return score;
		
		//return score - Game.getTotalTime();
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

package alg.genProgAlgorithm.fitnessFunction;

import java.util.ArrayList;

public class CutreFitness implements FitnessFunctionInterface {

	@Override
	public double f(ArrayList<Double> params) {
		double score = params.get(0);
		
		return 100000 - score;
	}
	
	public double fitnessToPoints(double fitness) {
		
		return 100000 - fitness;
}

	@Override
	public boolean isMinimization() {
		return true;
	}

	@Override
	public String getName() {
		return "Cutre Fitness";
	}

}
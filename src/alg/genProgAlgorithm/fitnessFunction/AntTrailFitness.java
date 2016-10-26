package model.genProgAlgorithm.fitnessFunction;

import java.util.ArrayList;

public class AntTrailFitness implements FitnessFunctionInterface {

	@Override
	public double f(ArrayList<Double> params) {
		int foodEaten = params.get(0).intValue();
		//int steps = params.get(1).intValue();
		//int maxSteps = params.get(2).intValue();
		//int height = params.get(3).intValue();
		//int maxHeight = params.get(4).intValue();
		
		return foodEaten
				//+ ( (1 - (steps / maxSteps)) * 10 ) // if it uses less steps gets better fitness
				//- Math.max((height - maxHeight), 0) // subtract the surplus of levels if there's any
				;
	}

	@Override
	public boolean isMinimization() {
		return false;
	}

	@Override
	public String getName() {
		return "Ant Trail Fitness";
	}

}

package jeco.core.operator.evaluator.fitness;

import pacman.game.util.GameInfo;

public class LevelsCompletedFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(GameInfo gi) {
		return 100 - gi.getAvgLastLevelReached();
	}
	
	public static double fitnessToPoints(double fitness) {
		return 100 - fitness;
	}
	
	@Override
	public double worstFitness() {
		return 100;
	}

	@Override
	public String getName() {
		return "Number of levels completed";
	}

}

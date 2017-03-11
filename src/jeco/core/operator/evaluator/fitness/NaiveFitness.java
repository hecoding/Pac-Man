package jeco.core.operator.evaluator.fitness;

import pacman.game.util.GameInfo;

public class NaiveFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(GameInfo gi) {
		return 100000 - gi.getAvgScore();
	}
	
	public static double fitnessToPoints(double fitness) {
		return 100000 - fitness;
	}
	
	@Override
	public double worstFitness() {
		return 100000;
	}

	@Override
	public String getName() {
		return "Naive Fitness";
	}

}

package jeco.core.operator.evaluator.fitness;

import pacman.game.util.GameInfo;

public class GhostsEatenFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(GameInfo gi) {
		return 1000 - gi.getAvgGhostsEaten();
	}
	
	public static double fitnessToPoints(double fitness) {
		return 1000 - fitness;
	}
	
	@Override
	public double worstFitness() {
		return 1000;
	}

	@Override
	public String getName() {
		return "Number_of_ghosts_eaten";
	}

}

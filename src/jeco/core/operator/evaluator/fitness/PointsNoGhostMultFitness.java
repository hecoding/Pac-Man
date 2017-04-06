package jeco.core.operator.evaluator.fitness;

import pacman.game.Constants;
import pacman.game.util.GameInfo;

public class PointsNoGhostMultFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(GameInfo gi) {
		double points = gi.getAvgPillsEaten()*Constants.PILL;
		points += gi.getAvgPowerPillsEaten()*Constants.POWER_PILL;
		points += gi.getAvgGhostsEaten()*Constants.GHOST_EAT_SCORE;
		return 100000 - points;
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
		return "Score without ghost-eaten multiplier";
	}

}

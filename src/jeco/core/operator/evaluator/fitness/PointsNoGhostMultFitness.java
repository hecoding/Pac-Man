package jeco.core.operator.evaluator.fitness;

import pacman.game.Constants;
import pacman.game.util.GameInfo;

public class PointsNoGhostMultFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(GameInfo gi) {
		double points = gi.getPillsEaten()*Constants.PILL;
		points += gi.getPowerPillsEaten()*Constants.POWER_PILL;
		points += gi.getGhostsEaten()*Constants.GHOST_EAT_SCORE;
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
		return "Points w/o ghost mult Fitness";
	}

}

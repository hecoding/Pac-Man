package jeco.core.operator.evaluator;

import java.util.ArrayList;

public class NaiveFitness implements FitnessEvaluatorInterface {

	@Override
	public double evaluate(ArrayList<Double> params) {
		double score = params.get(0);
		
		return 100000 - score;
	}
	
	public static double fitnessToPoints(double fitness) {
		
		return 100000 - fitness;
	}

}

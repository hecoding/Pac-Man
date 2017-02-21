package jeco.core.operator.evaluator;

import java.util.ArrayList;

public interface FitnessEvaluatorInterface {
	
	public double evaluate(ArrayList<Double> params);

	public String getName();
	
}

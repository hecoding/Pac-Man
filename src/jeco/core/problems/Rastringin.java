package jeco.core.problems;

import java.util.logging.Logger;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class Rastringin extends Problem<Variable<Double>> {
	private static final Logger logger = Logger.getLogger(Rastringin.class.getName());

	protected double bestValue = Double.POSITIVE_INFINITY;

	public Rastringin(Integer numberOfVariables) {
		super(numberOfVariables, 1);
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = -5.12;
			upperBound[i] = 5.12;
		}
	}

	public Solutions<Variable<Double>> newRandomSetOfSolutions(int size) {
		Solutions<Variable<Double>> solutions = new Solutions<Variable<Double>>();
		for (int i=0; i<size; ++i) {
			Solution<Variable<Double>> solI = new Solution<Variable<Double>>(numberOfObjectives);
			for (int j = 0; j < numberOfVariables; ++j) {
				Variable<Double> varJ = new Variable<Double>(RandomGenerator.nextDouble(lowerBound[j], upperBound[j]));
				solI.getVariables().add(varJ);
			}
			solutions.add(solI);
		}
		return solutions;
	}
	
	public void evaluate(Solutions<Variable<Double>> solutions) {
		for(Solution<Variable<Double>> solution : solutions)
			evaluate(solution);
	}

	public void evaluate(Solution<Variable<Double>> solution) {
		double fitness = 10*super.numberOfVariables;
		for(int i=0; i<numberOfVariables; ++i) {
			double xi = solution.getVariables().get(i).getValue();
			fitness += Math.pow(xi, 2) - 10*Math.cos(2*Math.PI*xi);
		}
		solution.getObjectives().set(0, fitness);
		if(fitness<bestValue) {
			logger.info("Best value found: " + bestValue);
			bestValue = fitness;
		}
	}

  public Rastringin clone() {
  	Rastringin clone = new Rastringin(this.numberOfVariables);
  	for(int i=0; i<numberOfVariables; ++i) {
  		clone.lowerBound[i] = lowerBound[i];
  		clone.upperBound[i] = upperBound[i];
  	}
  	return clone;
  }
}

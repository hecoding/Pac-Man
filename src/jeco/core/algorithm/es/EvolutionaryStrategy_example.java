package jeco.core.algorithm.es;

import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.Rastringin;

public class EvolutionaryStrategy_example {

	private static final Logger logger = Logger.getLogger(EvolutionaryStrategy_example.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<Variable<Double>>(problem);
		EvolutionaryStrategy<Variable<Double>> algorithm = new EvolutionaryStrategy<Variable<Double>>(problem, mutationOp, 5, 1, EvolutionaryStrategy.SELECTION_PLUS, 10, 250, true);
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		for (Solution<Variable<Double>> solution : solutions) {
			logger.info("Fitness = " + solution.getObjectives().get(0));
		}
	}
}

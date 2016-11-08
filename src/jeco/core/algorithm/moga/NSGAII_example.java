package jeco.core.algorithm.moga;

import java.util.logging.Logger;

import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.dtlz.DTLZ1;

public class NSGAII_example {
	private static final Logger logger = Logger.getLogger(NSGAII_example.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First create the problem
		DTLZ1 problem = new DTLZ1(30);
		// Second create the algorithm
		NSGAII<Variable<Double>> algorithm = new NSGAII<Variable<Double>>(problem, 100, 250, new PolynomialMutation<Variable<Double>>(problem), new SBXCrossover<Variable<Double>>(problem), new BinaryTournamentNSGAII<Variable<Double>>());
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
}

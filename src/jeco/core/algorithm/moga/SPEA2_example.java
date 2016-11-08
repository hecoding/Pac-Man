package jeco.core.algorithm.moga;

import java.util.logging.Logger;

import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.zdt.ZDT1;

public class SPEA2_example {
	private static final Logger logger = Logger.getLogger(SPEA2_example.class.getName());
	public static void main(String[] args) {
		// First create the problem
		ZDT1 zdt1 = new ZDT1(30);
		// Second create the algorithm
		SPEA2<Variable<Double>> spea2 = new SPEA2<Variable<Double>>(zdt1, 100, 250, new PolynomialMutation<Variable<Double>>(zdt1), new SBXCrossover<Variable<Double>>(zdt1), new BinaryTournament<Variable<Double>>());
		Solutions<Variable<Double>> solutions = spea2.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
} // SPEA2_main.java


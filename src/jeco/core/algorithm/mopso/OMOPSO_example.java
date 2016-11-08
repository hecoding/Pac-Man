package jeco.core.algorithm.mopso;

import java.util.logging.Logger;

import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.zdt.ZDT1;

public class OMOPSO_example {
	private static final Logger logger = Logger.getLogger(OMOPSO_example.class.getName());

	public static void main(String[] args) throws Exception {
		// First create the problem
		ZDT1 problem = new ZDT1();
		OMOPSO<Variable<Double>> algorithm = new OMOPSO<Variable<Double>>(problem, 100, 250);
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}//main
}

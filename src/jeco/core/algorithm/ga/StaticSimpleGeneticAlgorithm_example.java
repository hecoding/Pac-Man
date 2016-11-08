package jeco.core.algorithm.ga;

import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.Rastringin;

/**
 *
 * @author J. Manuel Colmenar
 */
public class StaticSimpleGeneticAlgorithm_example {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<Variable<Double>>(problem,20.0,0.0);
		SBXCrossover<Variable<Double>> crossoverOp = new SBXCrossover<Variable<Double>>(problem);
		SimpleDominance<Variable<Double>> comparator = new SimpleDominance<Variable<Double>>();
		BinaryTournament<Variable<Double>> selectionOp = new BinaryTournament<Variable<Double>>(comparator);
		StaticSimpleGeneticAlgorithm<Variable<Double>> ga = new StaticSimpleGeneticAlgorithm<Variable<Double>>(problem, 10, 10, true, mutationOp, crossoverOp, selectionOp);
		ga.initialize();
		Solutions<Variable<Double>> solutions = ga.execute();
		for(Solution<Variable<Double>> solution : solutions) {
			System.out.println("Fitness = " + solution.getObjectives().get(0));
		}
		//System.out.println("solutions.size()="+ solutions.size());
		//System.out.println(solutions.toString());
		//System.out.println("solutions.size()="+ solutions.size());
	}
}

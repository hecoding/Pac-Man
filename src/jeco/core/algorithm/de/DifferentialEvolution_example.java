package jeco.core.algorithm.de;

import java.util.logging.Logger;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.Rastringin;

/**
 * Test class and example of Differential Evolution use.
 *
 * @author J. M. Colmenar
 */
public class DifferentialEvolution_example {

    private static final Logger logger = Logger.getLogger(DifferentialEvolution_example.class.getName());

    public static void main(String[] args) {
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm:
        /* DifferentialEvolution(Problem<Variable<Double>> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved,
            Double mutationFactor, Double recombinationFactor) */
        DifferentialEvolution algorithm = new DifferentialEvolution(problem, 20, 250, true, 1.0, 0.5);
        algorithm.initialize();
        algorithm.verbose = true;
        Solutions<Variable<Double>> solutions = algorithm.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            logger.info("Fitness = " + solution.getObjectives().get(0));
        }
    }
}

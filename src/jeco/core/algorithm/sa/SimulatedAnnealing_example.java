package jeco.core.algorithm.sa;

import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.problems.Rastringin;

/**
 * Class to test the SA solver.
 *
 * @author J. M. Colmenar
 */
public class SimulatedAnnealing_example {

    private static final Logger logger = Logger.getLogger(SimulatedAnnealing_example.class.getName());

    /**
     * @param args
     */
    public static void main(String[] args) {
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        SimulatedAnnealing<Variable<Double>> algorithm = new SimulatedAnnealing<Variable<Double>>(problem, (long)100000, (long)0);
        algorithm.initialize();
        Solutions<Variable<Double>> solutions = algorithm.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            logger.log(Level.INFO, "Fitness = " + solution.getObjectives().get(0));
        }
    }
}

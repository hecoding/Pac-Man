package jeco.core.algorithm.sa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeco.core.algorithm.Algorithm;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Class implementing the simulated annealing technique for problem solving.
 *
 * Works only for one objective.
 *
 * Does not require temperature to be given because it automatically adapts the
 * parameters: Natural Optimization [de Vicente et al., 2000]
 *
 * @author J. M. Colmenar
 */
public class SimulatedAnnealing<T extends Variable<?>> extends Algorithm<T> {

    /**
     * Maximum number of iterations
     */
    private long maxIterations = 10000;
    private long currentMoves = 0;
    /**
     * Maximum time in seconds. If 0, then consider maxIterations
     */
    private long maxSeconds = 0;
    private long startTime;
    private boolean change;
    private long numChanges = 0;
    private final int LOG_RATIO = 1000;

    /* Cost-related attributes */
    private double currentMinimumCost = Double.MAX_VALUE;
    private double initialCost = Double.MAX_VALUE;
    private double k = 1.0;
    private final int OBJECTIVE = 0;
    private Comparator<Solution<T>> solutionComparator;
    /**
     * Current population. Will be just one solution.
     */
    private Solutions<T> currPopulation;
    private final int BESTSOL = 0;
    /**
     * Random number generator
     */
    private static Random rnd;
    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(SimulatedAnnealing.class.getName());
    /**
     * Nome of the log file
     */
    public static String logFile = "objectives_log.txt";

    /**
     * This constructor allows to establish the maximum number of iterations.
     *
     * @param problem
     * @param maxIter number of iterations where the search will stop.
     * @param randomSeed seed for random number generation
     */
    public SimulatedAnnealing(Problem<T> problem, Long maxIter, Long randomSeed) {
        super(problem);

        maxIterations = maxIter;
        rnd = new Random(randomSeed);

        // Comparator for objective number OBJECTIVE
        solutionComparator = new Comparator<Solution<T>>() {
            @Override
            public int compare(Solution<T> t, Solution<T> t1) {
                if (t.getObjective(OBJECTIVE) < t1.getObjective(OBJECTIVE)) {
                    return -1;
                } else {
                    if (t.getObjective(OBJECTIVE) > t1.getObjective(OBJECTIVE)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        };

    }

    /**
     * Parameterized constructor
     *
     * @param maxIter number of iterations where the search will stop.
     * @param k is the weight of the temperature
     * @param stopWhenFeasible stops the search if finds a feasible solution
     * @param randomSeed seed for random number generation
     * @param maxSecs is maximum execution time in seconds. If 0, then consider
     * max iterations
     * @param currLogFile is the name of the log file.
     */
    public SimulatedAnnealing(Problem<T> problem, Long maxIter, double k,
            Long randomSeed, Long maxSecs, String currLogFile) {
        this(problem, maxIter, randomSeed);
        this.k = k;
        maxSeconds = maxSecs;
        logFile = currLogFile;
    }

    
    @Override
    public void initialize() {
        // Start from a random solution
        currPopulation = this.problem.newRandomSetOfSolutions(1);
        Solution<T> bestSolution = currPopulation.get(BESTSOL);
        problem.evaluate(bestSolution);
        
        numChanges = 0;
        change = false;

        // Clean log file:
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(logFile)));
            writer.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        // Logging SA parameters
        String logStr = "\n# SA Parameters:";
        logStr += "\n-> K (weight for temperature): " + k;
        logStr += "\n-> Max. iterations: " + maxIterations;
        logStr += "\n-> Max. time: " + maxSeconds + " seconds";
        logStr += "\n";

        logger.log(Level.INFO, logStr);

        initialCost = bestSolution.getObjective(OBJECTIVE);

        startTime = System.currentTimeMillis();
        
        // Log starting point:
        logObjectives();

    }

    
    @Override
    public void step() {
        Solution<T> bestSolution = currPopulation.get(BESTSOL);
        currentMinimumCost = bestSolution.getObjective(OBJECTIVE);

        // Obtain a neighbour (next state)
        Solution<T> newSolution = neighbourSolution(bestSolution);
        problem.evaluate(newSolution);

        currentMoves++;

        /* Compute neighbour's (state) energy and check if move to
         the neighbour (state) */

        /* If new solution is has best objetive value, change */
        if (newSolution.compareTo(bestSolution, solutionComparator) < 0) {
            change = true;
        } else {
            // If new solution is worse, change depends on probability
            if (changeState(bestSolution, newSolution)) {
                change = true;
            }
        }

        if (change) {
            numChanges++;
            currPopulation.remove(BESTSOL);
            currPopulation.add(newSolution);
            // Txt for objectives
            logObjectives();
            // Logs detail only if solution changes and following the ratio
            if ((numChanges % LOG_RATIO) == 0) {
                // Screen and also backups solution to XML file
                String logStr = "\n# SA -- Iterations: " + currentMoves + " -- Current SA Temperature: " + Double.toString(getTemperature()) + "\n";
                logStr += "-- Current Best Solution: " + bestSolution + "\n";
                logStr += "Time: " + getCurrentTimeInSeconds() + " seconds.\n";
                logger.log(Level.INFO, logStr);
            }
            change = false;
        }

    }

    
    @Override
    public Solutions<T> execute() {
        boolean stopSA = false;

        while (!stopSA) {
            step();

            if (maxSeconds == 0) {
                stopSA = (currentMoves == maxIterations);
            } else {
                stopSA = getCurrentTimeInSeconds() >= maxSeconds;
            }

        }

        logObjectives();
        String logStr = "\n# TOTAL SA -- Iterations: " + currentMoves + " -- Current SA Temperature: " + Double.toString(getTemperature()) + "\n";
        logStr += "TOTAL Time: " + getCurrentTimeInSeconds() + " seconds.\n";
        logger.log(Level.INFO, logStr);

        return currPopulation;

    }

    
    /**
     * Computes probability of changing to new solution. It considers ONLY one
     * objective for energy.
     *
     * @param oldSol current state
     * @param newSol possible next state
     * @return true if probability gives chance to change state, false otherwise
     */
    private boolean changeState(Solution<T> oldSol, Solution<T> newSol) {

        // Higher cost means new energy to be higher than old energy
        double energyDiff;
        energyDiff = newSol.getObjective(OBJECTIVE) - oldSol.getObjective(OBJECTIVE);

        // Compute probability. Must be between 0 and 1.
        double temp = getTemperature();
        double prob = Math.exp(-energyDiff / temp);

        // nextDouble returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
        if (rnd.nextDouble() <= prob) {
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * Obtains the temperature, which is naturally adapted to evolution of the
     * search.
     */
    private double getTemperature() {
        return k * Math.abs((currentMinimumCost - initialCost) / currentMoves);

    }

    
    /**
     * Logs time and objective values of the best solution to the current
     * logFile
     */
    private void logObjectives() {
        // File log code
        try {
            // Appending
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(logFile), true));
            // # Time Objective
            writer.write(getCurrentTimeInSeconds() + " " + currPopulation.get(BESTSOL).getObjective(OBJECTIVE) + "\n");
            writer.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns current time in seconds since algorithm started.
     *
     * @return
     */
    private Double getCurrentTimeInSeconds() {
        return ((System.currentTimeMillis() - startTime) / 1000.0);
    }
    
    
    
    /**
     * Returns a solution where just one variable was changed:
     *
     * @return
     */
    private Solution<T> neighbourSolution(Solution<T> sol) {
        // Generate a brand new solution
        ArrayList<T> variables = problem.newRandomSetOfSolutions(1).get(0).getVariables();
        // Randomly choose one variable
        int i = rnd.nextInt(variables.size());
        // Clone current solution and introduce change.
        Solution newSol = sol.clone();
        newSol.getVariable(i).setValue(variables.get(i).getValue());
        return newSol;
    }
    
    
}

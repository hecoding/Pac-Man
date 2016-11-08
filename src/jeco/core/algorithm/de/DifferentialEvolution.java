package jeco.core.algorithm.de;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;
import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Class implementing the differential evolution technique for problem solving.
 * 
 * @author J. M. Colmenar
 */
public class DifferentialEvolution extends Algorithm<Variable<Double>> {

    private static final Logger logger = Logger.getLogger(SimpleGeneticAlgorithm.class.getName());
    private static Random rnd;

    /////////////////////////////////////////////////////////////////////////
    public Boolean verbose = false;    
    public Boolean ceilAndFloor = false;
    
    protected Boolean stopWhenSolved = null;
    protected Integer maxGenerations = null;
    protected Integer currentGeneration = null;
    protected Solutions<Variable<Double>> population;
    /////////////////////////////////////////////////////////////////////////

    protected Integer np = null;            // Parent number (poblacion)
    protected double f = 0;                 // Mutation factor
    protected double gr = 0;                 // Recombination factor
    
    /////////////////////////////////////////////////////////////////////////
    protected SimpleDominance<Variable<Double>> dominance = new SimpleDominance<Variable<Double>>();
    /////////////////////////////////////////////////////////////////////////
    protected HashSet<Integer> alreadyChosen = new HashSet<Integer>();
    
    
    /**
     * Class constructor.
     * @param problem
     */
    public DifferentialEvolution(Problem<Variable<Double>> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved,
            Double mutationFactor, Double recombinationFactor) {
        super(problem);
        
        this.maxGenerations = maxGenerations;
        this.np = maxPopulationSize;
        this.stopWhenSolved = stopWhenSolved;
        
        this.f = mutationFactor;
        this.gr = recombinationFactor;
        
        rnd = new Random();
        
        if (np < 4) {
            logger.severe("Differential Evolution requieres at least 4 individuals !!");
            System.exit(-1);
        }
        
        if ((f<0) || (f>2)) {
            logger.severe("Differential Evolution requieres mutation factor within range [0,2]");
            System.exit(-1);            
        }
        
        if ((gr<0) || (gr>1)) {
            logger.severe("Differential Evolution requieres recombination factor within range [0,1]");
            System.exit(-1);            
        }        
    }
        
    
    @Override
    public void initialize() {
        population = problem.newRandomSetOfSolutions(np);
        problem.evaluate(population);
        Collections.sort(population, dominance);        
        currentGeneration = 0;
    }

    
    /**
     * Returns a target vector from the population selected by random
     * not selected before, and different to i.
     * 
     * @return 
     */
    protected Solution<Variable<Double>> targetVector(int i) {
        
        int k = 0;
        
        do {
            k = rnd.nextInt(np);
        } while ((k==i) || (alreadyChosen.contains(k)));
        
        alreadyChosen.add(k);
        
        return population.get(k);
        
    }
    
    
    @Override
    public void step() {
        currentGeneration++;
        
        // Mutation phase ******************************************************
        
        // Create the muted population
        Solutions<Variable<Double>> noisyVectors = new Solutions<Variable<Double>>();

        for (int i = 0; i < np; i++) {
            // Target vectors are selected:
            Solution<Variable<Double>> xa = targetVector(i);
            Solution<Variable<Double>> xb = targetVector(i);
            Solution<Variable<Double>> xc = targetVector(i);

            Solution<Variable<Double>> v = population.get(i);
            // This clone is done just to have a new solution object. Content will be overwritten.
            Solution<Variable<Double>> noisyVector = v.clone();

            for (int j = 0; j < v.getVariables().size(); j++) {
                double value = xc.getVariable(j).getValue() + (f * (xa.getVariable(j).getValue() - xb.getVariable(j).getValue()));
                if (ceilAndFloor) {
                    // Ceil and floor for mutated values
                    if (value < problem.getLowerBound(j)) {
                        value = problem.getLowerBound(j);
                    } else {
                        if (value > problem.getUpperBound(j)) {
                            value = problem.getUpperBound(j);
                        }
                    }
                }
                noisyVector.getVariable(j).setValue(value);
            }

            noisyVectors.add(noisyVector);            
            
            // Clean selected targets:
            alreadyChosen.clear();
        }
        
        
        // Recombination phase ******************************************************
        
        // Create the recombined population
        Solutions<Variable<Double>> trialVectors = new Solutions<Variable<Double>>();

        for (int i = 0; i < np; i++) {

            Solution<Variable<Double>> t = population.get(i).clone();

            for (int j = 0; j < t.getVariables().size(); j++) {
                if (rnd.nextDouble() < gr) {
                    t.getVariable(j).setValue(noisyVectors.get(i).getVariable(j).getValue());
                }
            }

            trialVectors.add(t);
            
        }
        
        
        // Selection phase ******************************************************
        
        // Current population is already evaluated. Not trial vectors:
        problem.evaluate(trialVectors);
        
        Solutions<Variable<Double>> newPopulation = new Solutions<Variable<Double>>();        
        
        for (int i = 0; i < np; i++) {
            
            // Minimizing !!
            if (trialVectors.get(i).getObjective(0) < population.get(i).getObjective(0)) {
                newPopulation.add(trialVectors.get(i));
            } else {
                newPopulation.add(population.get(i));
            }
            
        }
        
        // Reorder:
        population = newPopulation;
        Collections.sort(population, dominance);    
        
        if (verbose) {
            double minFitness = Double.MAX_VALUE;
            double maxFitness = 0.0;
            double acu = 0.0;
            for (Solution<Variable<Double>> s : population) {
                double obj = s.getObjective(0).doubleValue();
                acu += obj;
                if (obj < minFitness) {
                    minFitness = obj;
                }
                if (obj > maxFitness) {
                    maxFitness = obj;
                }
            }
            double avgFitness = acu / population.size();
            System.out.println("@ " + currentGeneration + ";" + minFitness + ";" + maxFitness + ";" + avgFitness);
        }        
        
    }

    
    
    @Override
    public Solutions<Variable<Double>> execute() {
        // Headers for detailed fitness info
        if (verbose) {
            System.out.println("@ # Gen.;Min Fit.;Max Fit.;Avg Fit.");
        }
        
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            Double bestObj = population.get(0).getObjectives().get(0);
            if (percentage == nextPercentageReport) {
                logger.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                nextPercentageReport += 10;
            }
            if(stopWhenSolved) {
            	if(bestObj<=0) {
            		logger.info("Optimal solution found in " + currentGeneration + " generations.");
            		break;
            	}
            }
        }
        return population;
    }

    
    
}

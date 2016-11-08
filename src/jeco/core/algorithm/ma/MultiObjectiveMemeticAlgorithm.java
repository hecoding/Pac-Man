package jeco.core.algorithm.ma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import jeco.core.algorithm.Algorithm;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.assigner.FrontsExtractor;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;
import org.apache.commons.math3.stat.StatUtils;

/**
 * This class implements a memetic algorithm that works on several
 * objectives, based in NSGAII + LS.
 * 
 * @author J. M. Colmenar
 */
public class MultiObjectiveMemeticAlgorithm<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger logger = Logger.getLogger(MultiObjectiveMemeticAlgorithm.class.getName());
    /////////////////////////////////////////////////////////////////////////
    protected int maxGenerations;
    protected int maxPopulationSize;
    /////////////////////////////////////////////////////////////////////////
    protected Comparator<Solution<V>> dominance;
    protected int currentGeneration;
    protected Solutions<V> population;
    protected Solutions<V> eliteSet;
    protected double lsProbabilityInGeneration;

    public Solutions<V> getPopulation() {
        return population;
    }
    protected MutationOperator<V> mutationOperator;
    protected CrossoverOperator<V> crossoverOperator;
    protected SelectionOperator<V> selectionOperator;
    protected LocalSearch<V> localSearch;
   
    public MultiObjectiveMemeticAlgorithm(Problem problem, Integer maxPopulationSize,
            Integer maxGenerations, MutationOperator mutationOperator,
            CrossoverOperator crossoverOperator, SelectionOperator selectionOperator,
            LocalSearch localSearch) {
        super(problem);

        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
        this.localSearch = localSearch;
        this.lsProbabilityInGeneration = 1.0;

    }

    public void setLsProbabilityInGeneration(double lsProbabilityInGeneration) {
        this.lsProbabilityInGeneration = lsProbabilityInGeneration;
    }

    @Override
    public void initialize() {
        dominance = new SolutionDominance<>();
        this.eliteSet = new Solutions<>();
        
        problem.setNumEvaluations(0);
        this.stop = false;
        // Create the initial solutionSet
        population = problem.newRandomSetOfSolutions(maxPopulationSize);
        problem.evaluate(population);
        currentGeneration = 0;
    }

    @Override
    public void step() {
        // Selection, crossover, mutation
        currentGeneration++;
        // Create the offSpring solutionSet        
        Solutions<V> childPop = new Solutions<>();
        Solution<V> parent1, parent2;
        for (int i = 0; i < (maxPopulationSize / 2); i++) {
            //obtain parents
            parent1 = selectionOperator.execute(population).get(0);
            parent2 = selectionOperator.execute(population).get(0);
            Solutions<V> offSpring = crossoverOperator.execute(parent1, parent2);
            for (Solution<V> solution : offSpring) {
                mutationOperator.execute(solution);
                childPop.add(solution);
            }
        } // for
        problem.evaluate(childPop);
        
        // Run the local search only in non-dominated
        Solutions<V> nonDominated = new Solutions<>();
        nonDominated.addAll(childPop);
        nonDominated.addAll(population);
        nonDominated.reduceToNonDominated(dominance);
        
        double p = RandomGenerator.nextDouble();
        if (p <= lsProbabilityInGeneration) {
            // LS is run in this generation.
            Solutions<V> afterLS = new Solutions<>();
            for (Solution<V> s : nonDominated) {
                afterLS.add(localSearch.doLocalSearch(problem,dominance,s));
            }
            // Union of child and afterLS, and reduce population.
            childPop.addAll(afterLS);
        } else {
            // Keep the non-dominated
            childPop.addAll(nonDominated);
        }
        
        childPop.addAll(population);
        
        // Reducing the union
        population = reduce(childPop, maxPopulationSize);
        
        // Maintain eliteSet
        eliteSet.addAll(population);
        eliteSet.reduceToNonDominated(dominance);
        
        if (problem.reachedMaxEvaluations()) {
            this.stopExection();
        }
        
    }

    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
        while ((currentGeneration < maxGenerations) && !stop) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            if (percentage == nextPercentageReport) {
                double[][] objs = new double[problem.getNumberOfObjectives()][population.size()];
                String logStr = "";
                for (int i=0; i<objs.length; i++) {
                    for (int j=0; j<objs[0].length; j++) {
                        objs[i][j] = population.get(j).getObjective(i);
                    }
                    logStr += " - Obj. "+i+": Max. "+StatUtils.max(objs[i])+"; Avg. "+StatUtils.mean(objs[i])+"; Min. "+StatUtils.min(objs[i]);
                }
                logger.info(percentage + "% performed ... "+logStr);
                nextPercentageReport += 10;
            }

        }
        return this.getCurrentSolution();       
    }

    public Solutions<V> getCurrentSolution() {
        // Add elite Set and reduce
        population.addAll(eliteSet);
        population.reduceToNonDominated(dominance);
        return population;
    }    
 
    /**
     * Reduction like is made in NSGA-II.
     *
     * @param pop
     * @param maxSize
     * @return
     */
    public Solutions<V> reduce(Solutions<V> pop, int maxSize) {
        FrontsExtractor<V> extractor = new FrontsExtractor<>(dominance);
        ArrayList<Solutions<V>> fronts = extractor.execute(pop);

        Solutions<V> reducedPop = new Solutions<>();
        CrowdingDistance<V> assigner = new CrowdingDistance<>(problem.getNumberOfObjectives());
        Solutions<V> front;
        int i = 0;
        while (reducedPop.size() < maxSize && i < fronts.size()) {
            front = fronts.get(i);
            assigner.execute(front);
            reducedPop.addAll(front);
            i++;
        }

        ComparatorNSGAII<V> comparator = new ComparatorNSGAII<>();
        if (reducedPop.size() > maxSize) {
            Collections.sort(reducedPop, comparator);
            while (reducedPop.size() > maxSize) {
                reducedPop.remove(reducedPop.size() - 1);
            }
        }
        return reducedPop;
    }

    
}

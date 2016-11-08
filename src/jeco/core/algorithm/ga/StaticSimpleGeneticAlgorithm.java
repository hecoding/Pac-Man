package jeco.core.algorithm.ga;

import java.util.Collections;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Simple GA with a different offspring replacement that is more static because
 * it does not replaces all the population but only the 2 worst individuals.
 * 
 * @author J. Manuel Colmenar
 */
public class StaticSimpleGeneticAlgorithm<V extends Variable<?>> extends SimpleGeneticAlgorithm<V> {

    public StaticSimpleGeneticAlgorithm(Problem problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator mutationOperator, CrossoverOperator crossoverOperator, SelectionOperator selectionOperator) {
        super(problem, maxPopulationSize, maxGenerations, stopWhenSolved, mutationOperator, crossoverOperator, selectionOperator);
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet        
        Solutions<V> childPop = new Solutions<V>();
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
        
        // Replacement
        population = replacement(population,childPop);

        //Actualize the archive
        for (Solution<V> solution : population) {
            Solution<V> clone = solution.clone();
            leaders.add(clone);
        }
        reduceLeaders();
        StringBuilder buffer = new StringBuilder();
        buffer.append("@ ").append(currentGeneration).append(";").append(leaders.get(0).getObjective(0));
        buffer.append(";").append(leaders.get(leaders.size() - 1).getObjective(0)).append(";").append(leaders.get(leaders.size() / 2).getObjective(0));
        logger.fine(buffer.toString());

    }

    
    /**
     * Merges the population with the offspring removing the worst 2 individuals
     * of the population and including the 2 better offspring.
     * 
     * @param population
     * @param childPop
     * @return 
     */
    private Solutions<V> replacement(Solutions<V> population, Solutions<V> offspring) {
        Collections.sort(offspring, dominance);
        population.add(offspring.get(0));
        population.add(offspring.get(1));
        Collections.sort(population, dominance);
        population.remove(population.size() - 1);
        population.remove(population.size() - 1);

        return population;
    }

}

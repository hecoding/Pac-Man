package jeco.core.algorithm.ge;

import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Grammatical evolution using just one objective.
 * 
 * @author J. M. Colmenar
 */
public class SimpleGrammaticalEvolution extends Algorithm<Variable<Integer>> {

    SimpleGeneticAlgorithm<Variable<Integer>> algorithm;
    
    public SimpleGrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem);
        
        // Algorithm operators
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<Variable<Integer>>(problem, probMutation);
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<Variable<Integer>>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<Variable<Integer>>(comparator);
        
        algorithm = new SimpleGeneticAlgorithm<Variable<Integer>>(problem, 
                maxPopulationSize, maxGenerations, true, mutationOperator, crossoverOperator, selectionOp);
        
    }

    @Override
    public void initialize() {
        algorithm.initialize();
    }

    @Override
    public void step() {
        algorithm.step();
    }

    @Override
    public Solutions<Variable<Integer>> execute() {
        return algorithm.execute();
    }

}

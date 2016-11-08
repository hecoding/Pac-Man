package jeco.core.algorithm.es;

import java.util.Collections;
import java.util.logging.Logger;

import jeco.core.algorithm.Algorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class EvolutionaryStrategy<T extends Variable<?>> extends Algorithm<T> {

	private static final Logger logger = Logger.getLogger(EvolutionaryStrategy.class.getName());

	public static final int SELECTION_COMMA = 0;
	public static final int SELECTION_PLUS = 1;
	public static final int SELECTION_DEFAULT = SELECTION_PLUS;

	public static final int RHO_DEFAULT = 1;

	/////////////////////////////////////////////////////////////////////////
	protected boolean stopWhenSolved;
	protected int maxGenerations;
	protected int mu;
	protected int selectionType;
	protected int rho;
	protected int lambda;    
	/////////////////////////////////////////////////////////////////////////
	protected SimpleDominance<T> dominance = new SimpleDominance<T>();
	protected int currentGeneration;
	protected Solutions<T> muPopulation;
	protected MutationOperator<T> mutationOperator;
	protected SinglePointCrossover<T> crossoverOperator;
	protected SelectionOperator<T> selectionOperator;

	public EvolutionaryStrategy(Problem<T> problem, MutationOperator<T> mutationOperator, int mu, int rho, int selectionType, int lambda, int maxGenerations, boolean stopWhenSolved) {
		super(problem);
		this.mu = mu;
		this.rho = rho;
		this.selectionType = selectionType;
		this.lambda = lambda;
		this.maxGenerations = maxGenerations;
		this.mutationOperator = mutationOperator;
		this.crossoverOperator = new SinglePointCrossover<T>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.ALLOW_REPETITION);
		this.selectionOperator = new BinaryTournament<T>(new SimpleDominance<T>());
		this.stopWhenSolved = stopWhenSolved;
	}

	public EvolutionaryStrategy(Problem<T> problem, MutationOperator<T> mutationOperator, int mu, int lambda, int maxGenerations) {
		this(problem, mutationOperator, mu, 1, SELECTION_PLUS, lambda, maxGenerations, true);
	}
	
	@Override
	public void initialize() {
		// Create the initial solutionSet
		muPopulation = problem.newRandomSetOfSolutions(mu);
		problem.evaluate(muPopulation);
		currentGeneration = 0;
	}

	@Override
	public Solutions<T> execute() {
		int nextPercentageReport = 10;
		while (currentGeneration < maxGenerations) {
			step();
			int percentage = Math.round((currentGeneration * 100) / maxGenerations);
			if (percentage == nextPercentageReport) {
				logger.info(percentage + "% performed ...");
				nextPercentageReport += 10;
			}
			if(stopWhenSolved) {
				Double bestObj = muPopulation.get(0).getObjectives().get(0);
				if(bestObj<=0) {
					logger.info("Optimal solution found in " + currentGeneration + " generations.");
					break;
				}
			}
		}
		return muPopulation;
	}

	public void step() {
		currentGeneration++;
		// Create the offSpring solutionSet  
		Solutions<T> lambdaPopulation = new Solutions<T>();
		Solution<T> parent1, parent2;
		while(lambdaPopulation.size()<lambda) {
			// We apply recombination if rho is 2 or greater
			// In this version we only apply a traditional recombination of two parents
			Solutions<T> offSpring = new Solutions<T>();
			parent1 = selectionOperator.execute(muPopulation).get(0);
			if(rho>RHO_DEFAULT) {
				// obtain 2º parent
				parent2 = selectionOperator.execute(muPopulation).get(0);
				offSpring.addAll(crossoverOperator.execute(parent1, parent2));
			}
			else {
				offSpring.add(parent1.clone());
			}
			for (Solution<T> solution : offSpring) {
				mutationOperator.execute(solution);
				lambdaPopulation.add(solution);
			}        	
		} // for
		problem.evaluate(lambdaPopulation);
		// Selection
		if(selectionType == SELECTION_PLUS) {
			lambdaPopulation.addAll(muPopulation);
		}
		// Reorder and reduce:
		Collections.sort(lambdaPopulation, dominance);
		while(lambdaPopulation.size()>mu)
			lambdaPopulation.remove(lambdaPopulation.size()-1);
		muPopulation = lambdaPopulation;
	} // step

}
